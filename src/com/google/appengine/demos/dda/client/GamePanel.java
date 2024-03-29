package com.google.appengine.demos.dda.client;

import com.google.appengine.demos.dda.shared.*;
import com.google.appengine.demos.dda.shared.values.PlayerValue;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO(tobyr): Doc me
 *
 * @author Toby Reyelts
 */
public class GamePanel extends VerticalPanel {

  Image simonImage = new Image();
  Label roundLabel = new Label();
  Label timeLabel = new Label();
  Label scoreLabel = new Label();
  Label gameLabel = new Label();
  Label actionLabel = new Label();
  Map<String,Image> playerImages = new HashMap<String,Image>();
  Map<String,Label> scoreLabels = new HashMap<String,Label>();
  Map<String, Integer> playerScores = new HashMap<String, Integer>();
  Map<String, Integer> playerSequences = new HashMap<String, Integer>();
  static final int numRows = 4;
  static final int numColumns = 4;
  Grid playerGrid = new DynamicGrid(numRows, numColumns);
  String playerName;
  HTML statusText = new HTML();
  GameServiceAsync gameService = GameService.App.getInstance();
  Timer gameStartTimer;

  int score;
  int seq;

  // Per-dance state
  boolean animating;
  boolean readyForInput;

  /**
   * The current dance being executed.
   */
  DanceBeginMessage danceMessage;

  /**
   * The current expected Step from the player.
   */
  int currentStep;

  int correctStepsTaken;

  GamePanel(String playerName, final Date estimatedStartTime) {
    this.playerName = playerName;
    gameLabel.addStyleDependentName("gameLabel");
    roundLabel.setText("Waiting for more players!");
    updateTimeUntilGameStart(estimatedStartTime);
    (gameStartTimer = new Timer() {
      @Override
      public void run() {
        updateTimeUntilGameStart(estimatedStartTime);
      }
    }).scheduleRepeating(500);
    scoreLabel.setText("Score: ");
    add(roundLabel);
    add(timeLabel);
    add(scoreLabel);
    add(gameLabel);
    add(actionLabel);
    HorizontalPanel avatarPanel = new HorizontalPanel();
    add(avatarPanel);
    simonImage.setResource(Resources.instance.androidLargeImage());
    simonImage.addMouseDownHandler(new MouseDownHandler() {
      public void onMouseDown(MouseDownEvent event) {
        // Keep the image from being selected when we accidentally
        // drag over it.
        event.preventDefault();
      }
    });
    simonImage.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent clickEvent) {
        if (animating || !readyForInput) {
          return;
        }
        actionLabel.setText("");
        NativeEvent event = clickEvent.getNativeEvent();
        int x = getRelativeX(event, simonImage);
        int y = getRelativeY(event, simonImage);
        Step step = ImageMap.getStep(x, y);
        stepAnimateLocal(simonImage, step);
      }
    });
    avatarPanel.add(simonImage);
    avatarPanel.add(playerGrid);
    add(statusText);
  }

  private void updateTimeUntilGameStart(Date estimatedStartTime) {
    long currentMillis = System.currentTimeMillis();
    long startMillis = estimatedStartTime.getTime();
    int timeLeft = Math.max(0, (int)(startMillis - currentMillis)) / 1000;
    timeLabel.setText("Time until game start: Less than " + timeLeft + " seconds.");
  }

  /**
   * Receives messages pushed from the server.
   */
  public void receiveMsg(Message msg) {
    switch (msg.getType()) {
      case GAME_BEGIN:
        beginGame();
      break;

      case MATCH_BEGIN:
      case ROUND_BEGIN:
      break;

      case DANCE_BEGIN:
        startDancing((DanceBeginMessage)msg);
      break;

      case NEW_PLAYER:
        addPlayer((NewPlayerMessage)msg);
      break;

      case STEP_OCCURRED:
        stepOccurred((StepOccurredMessage)msg);
      break;

      case GAME_END:
        endGame((GameEndMessage)msg);
      break;

      default:
        Window.alert("Unknown game type: " + msg.getType());
    }
  }

  private void addPlayer(NewPlayerMessage msg) {
    playerGrid.add(getPlayerWidget(msg.getPlayer()));
  }

  private void stepOccurred(StepOccurredMessage msg) {
    String key = msg.getPlayer().getKey();
    Integer oldSeq = playerSequences.get(key);
    if (oldSeq != null && oldSeq >= msg.getSequence()) {
      // We received a message out of sequence, ignore.
      return;
    }
    Image img = playerImages.get(key);
    stepAnimateRemote(img, msg.getStep());
    Label score = scoreLabels.get(key);
    score.setText("(" + msg.getScore() + ")");
    playerScores.put(key, msg.getScore());
    playerSequences.put(key, msg.getSequence());
  }

  private void beginGame() {
    gameStartTimer.cancel();
    roundLabel.setText("Round 1 of " + Main.getNumRounds());
    timeLabel.setText("Time Remaining:");
  }

  private void endGame(GameEndMessage message) {
    displayAward(playerName, formatPlace(getPlace()) + " place");
  }

  private void displayAward(String name, String place) {
    final DialogBox box = new DialogBox(true, true);
    box.setWidget(new Image("/render-award?to=" + URL.encodeComponent(name) +
			    "&for=" + URL.encodeComponent(place)));
    box.setAnimationEnabled(true);
    box.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
      public void setPosition(int offsetWidth, int offsetHeight) {
        int left = (Window.getClientWidth() - offsetWidth) / 2 - 400;
        int top =  50;
        box.setPopupPosition(left, top);
      }
    });
  }

  private String formatPlace(int place) {
    if (place % 10 == 1) {
      return place + "st";
    } else if (place % 10 == 2) {
      return place + "nd";
    } else if (place % 10 == 3) {
      return place + "rd";
    } else {
      return place + "th";
    }
  }

  private int getPlace() {
    ArrayList<Integer> scores = new ArrayList<Integer>(playerScores.values());
    Collections.sort(scores);
    for (int i = 0; i < scores.size(); i++) {
      if (scores.get(i) >= score) {
        return i + 1;
      }
    }
    return Math.max(1, scores.size());
  }

  private Widget getPlayerWidget(PlayerValue player) {
    String playerName = player.getName();
    Image playerImage = new Image(Resources.instance.androidImage());
    playerImages.put(player.getKey(), playerImage);
    VerticalPanel panel = new VerticalPanel();
    panel.add(playerImage);
    String shortPlayerName = playerName.substring(0, Math.min(10, playerName.length()));
    boolean truncated = playerName.length() > 10;
    if (truncated) {
      shortPlayerName += "...";
    }
    Label playerLabel = new Label(shortPlayerName);
    playerLabel.setWidth(playerImage.getWidth() + "px");
    playerLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    panel.add(playerLabel);
    Label scoreLabel = new Label("(0)");
    scoreLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    scoreLabels.put(player.getKey(), scoreLabel);
    panel.add(scoreLabel);
    return panel;
  }

  class DanceAnimation extends Animation {
    double stepSize;
    private DanceBeginMessage danceMsg;

    void setDance(DanceBeginMessage danceMsg) {
      this.danceMsg = danceMsg;
      stepSize = 1.0f / danceMsg.getSteps().size();
    }

    @Override
    protected void onUpdate(double progress) {
      int currentStep = (int) (progress / (stepSize / 2));
//      statusText.setHTML(statusText.getHTML() + "<br>Current step: " + currentStep + " progress: " + progress);
      if (progress == 0 || currentStep % 2 == 1) {
        simonImage.setResource(Resources.instance.androidLargeImage());
        return;
      }

      currentStep /= 2;
//      statusText.setText(statusText.getText() + "\nCurrent image step: " + currentStep);
      ImageResource image;
      if (currentStep >= danceMsg.getSteps().size()) {
        image = Resources.instance.androidLargeImage();
      } else {
        Step step = danceMsg.getSteps().get(currentStep);
        image = step.getLargeImage();
      }
      simonImage.setResource(image);
    }

    @Override
    protected void onComplete() {
      listenForDanceInput(danceMsg);
    }

    @Override
    protected double interpolate(double progress) {
      return progress;
    }
  }

  private void resetDanceState() {
    simonImage.setResource(Resources.instance.androidLargeImage());
    ImageResource smallAndroid = Resources.instance.androidImage();
    for (Image img : playerImages.values()) {
      img.setResource(smallAndroid);
    }
    currentStep = 0;
    correctStepsTaken = 0;
    animating = false;
    readyForInput = false;
    actionLabel.setText("");
  }

  private void startDancing(final DanceBeginMessage danceMsg) {
    danceMessage = danceMsg;
    resetDanceState();
    setRoundText(danceMsg.getRound() + 1);
    gameLabel.setText("Watch!");
    final DanceAnimation animation = new DanceAnimation();
    animation.setDance(danceMsg);
    animation.run(danceMsg.getTimeoutMillis());
  }

  private void listenForDanceInput(DanceBeginMessage danceMsg) {
    readyForInput = true;
    gameLabel.setText("Repeat!");
    startCountdownTimer(danceMsg);
  }

  void stepAnimateRemote(final Image image, final Step step) {
    if (step== null) {
      image.setResource(Resources.instance.androidImageSad());
      return;
    }
    image.setResource(step.getImage());
    new Timer() {
      @Override
      public void run() {
        image.setResource(Resources.instance.androidImage());
      }
      // NB(tobyr) Make this something not static?
    }.schedule(300);
  }

  private void stepAnimateLocal(final Image image, final Step step) {
    if (step == null) {
      return;
    }

    animating = true;
    image.setResource(step.getLargeImage());
    final List<Step> steps = danceMessage.getSteps();
    Step expectedStep = steps.get(currentStep);
    ++currentStep;
    final boolean isValidStep = step == expectedStep;
    if (isValidStep && currentStep == steps.size()) {
      score += 10 * (danceMessage.getRound() + 1);
      setScoreText(score);
    }
    gameService.reportStep(isValidStep ? step : null, score, seq++, new AsyncCallback() {
      public void onFailure(Throwable caught) {
        Window.alert(caught.getMessage());
      }
      public void onSuccess(Object result) {
      }
    });

    new Timer() {
      @Override
      public void run() {
        animating = false;
        if (isValidStep) {
          image.setResource(Resources.instance.androidLargeImage());
          correctStepsTaken++;
          if (correctStepsTaken == steps.size()) {
            actionLabel.setText("Dance complete. Wait for the next dance.");
            readyForInput = false;
          } else {
            readyForInput = true;
          }
          return;
        }
        currentStep = 0;
        correctStepsTaken = 0;
        readyForInput = false;
        image.setResource(Resources.instance.androidLargeImageSad());
        actionLabel.setText("You missed a step! Wait for the next dance.");
      }
      // NB(tobyr) Make this something not static?
    }.schedule(300);
  }

  private void startCountdownTimer(final DanceBeginMessage msg) {
    long timeoutMillis = msg.getTimeoutMillis();
    final int round = msg.getRound();
    long current = System.currentTimeMillis();
    final long deadline = current + timeoutMillis;
    setTimeText((int)(timeoutMillis / 1000));
    setRoundText(round + 1);
    new Timer() {
      @Override
      public void run() {
        long millisRemaining = deadline - System.currentTimeMillis();
        int secondsRemaining = (int) ((millisRemaining / 1000.0) + 0.99);
        if (secondsRemaining > 0) {
          setTimeText(secondsRemaining);
        } else {
          cancel();
          readyForInput = false;
          setTimeText(0);
          if (msg.getRound() < 9) {
            gameLabel.setText("Time's up!");
          } else {
            gameLabel.setText("Game Over!");            
	    actionLabel.setText("Please wait while we generate your award...");
          }
        }
      }
    }.scheduleRepeating(300);
  }

  private void setRoundText(int round) {
    roundLabel.setText("Round " + round + " of " + Main.getNumRounds());
  }

  private void setTimeText(int seconds) {
      timeLabel.setText("Time Remaining: 0:" + ((seconds < 10) ? "0" : "") + seconds);
  }

  private void setScoreText(int score) {
    scoreLabel.setText("Score: " + score);
  }

  // NB(tobyr) Looks like an oversight that these don't exist in ClickHandler,
  // when they're available from MouseEvent (where we copied them from).
  private static int getRelativeX(NativeEvent event, Widget w) {
    Element target = w.getElement();
    return event.getClientX() - target.getAbsoluteLeft() + target.getScrollLeft() +
      target.getOwnerDocument().getScrollLeft();
  }

  private static int getRelativeY(NativeEvent e, Widget w) {
    Element target = w.getElement();
    return e.getClientY() - target.getAbsoluteTop() + target.getScrollTop() +
        target.getOwnerDocument().getScrollTop();
  }
}
