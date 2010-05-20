package com.google.appengine.demos.dda.client;

import com.google.appengine.demos.dda.client.channel.Channel;
import com.google.appengine.demos.dda.client.channel.ChannelFactory;
import com.google.appengine.demos.dda.client.channel.SocketListener;
import com.google.appengine.demos.dda.shared.LoginResults;
import com.google.appengine.demos.dda.shared.Message;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.List;

/**
 * The entry point for Dance Dance Robot.
 *
 * @author Toby Reyelts
 */
public class Main implements EntryPoint {

  FindGamePanel findGamePanel;
  RootPanel root;
  SerializationStreamFactory pushServiceStreamFactory;
  GameServiceAsync gameService;

  public void onModuleLoad() {
    // NB: A bit of voodoo magic going on here.
    pushServiceStreamFactory =(SerializationStreamFactory) PushService.App.getInstance();
    gameService = GameService.App.getInstance();

    root = RootPanel.get();
    root.setStyleName("root");
    findGamePanel = new FindGamePanel(this);
    root.add(findGamePanel);
  }

  /**
   * Continues the game workflow after login is complete.
   * @param playerName The name of the player.
   * @param results The results of the login.
   */
  public void loginComplete(final String playerName, LoginResults results) {
    final GamePanel gamePanel = new GamePanel(playerName, results.getEstimatedStartTime());

    Channel channel = ChannelFactory.createChannel(results.getChannelId());
    channel.open(new SocketListener() {
      public void onOpen() {
        gameService.confirmLogin(new AsyncCallback<List<Message>>() {
          public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
          }

          public void onSuccess(List<Message> messages) {
            for (Message msg : messages) {
              gamePanel.receiveMsg(msg);
            }
          }
        });
      }

      public void onMessage(String encodedData) {
        try {
          SerializationStreamReader reader = pushServiceStreamFactory.createStreamReader(encodedData);
          Message message = (Message) reader.readObject();
          gamePanel.receiveMsg(message);
        } catch (SerializationException e) {
          throw new RuntimeException("Unable to deserialize " + encodedData, e);
        }
      }
    });

    root.remove(findGamePanel);
    root.add(gamePanel);
  }

  static int getNumRounds() {
    String numRoundsStr = Window.Location.getParameter("num_rounds");
    try {
      return Integer.parseInt(numRoundsStr);
    } catch (NumberFormatException e) {
      // Use default
      return 10;
    }
  }

  static int getWaitTime() {
    String waitTimeSecondsStr = Window.Location.getParameter("wait_time");
    try {
      return Integer.parseInt(waitTimeSecondsStr);
    } catch (NumberFormatException e) {
      // Use default
      return 60;
    }
  }
}
