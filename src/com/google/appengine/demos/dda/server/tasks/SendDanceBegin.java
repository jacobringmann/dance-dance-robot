package com.google.appengine.demos.dda.server.tasks;

import com.google.appengine.demos.dda.server.Game;
import com.google.appengine.demos.dda.server.GameServiceImpl;
import com.google.appengine.demos.dda.server.PushServer;
import com.google.appengine.demos.dda.shared.DanceBeginMessage;
import com.google.appengine.demos.dda.shared.GameEndMessage;
import com.google.appengine.demos.dda.shared.Step;
import com.newatlanta.appengine.taskqueue.Deferred;

import javax.servlet.ServletException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
* TODO: Doc me.
*
* @author Toby
*/
public class SendDanceBegin implements Deferred.Deferrable {

  private static final int NUM_ROUNDS = 10;
  private static final int START_STEP_COUNT = 3;
  private static final int START_TIMEOUT = 6000;
  private static final Step[] steps = Step.values();

  private Game game;
  private DanceBeginMessage msg;
  private static SecureRandom rng = createSecureRandom();

  public SendDanceBegin(Game game, DanceBeginMessage msg) {
    this.game = game;
    this.msg = msg;
  }

  public void doTask() throws ServletException, IOException {
    if (msg.getRound() < NUM_ROUNDS) {
      PushServer.sendMessage(game.getPlayers(), msg);

      // TODO(tobyr) Enough time for Simon to do his dance, people to imitate him,
      // and a breather between rounds. We should ponder if it makes sense to
      // let the clients tell us when they're ready for the next round.
      // (The answer does not seem obvious).
      int nextRoundDelay = 5000 + msg.getTimeoutMillis() * 2;
      Deferred.defer(new SendDanceBegin(game, createRandomDance(msg.getRound() + 1)),
          GameServiceImpl.getTaskOptions().countdownMillis(nextRoundDelay));
    } else {
      PushServer.sendMessage(game.getPlayers(), new GameEndMessage());
    }
  }

  public static DanceBeginMessage createRandomDance(int round) {
    int timeoutMillis = START_TIMEOUT + (((int)((round + 1)/ 2)) * 1000);
    int numSteps = START_STEP_COUNT + ((int)((round + 1)/ 2));
    List<Step> newSteps = new ArrayList<Step>();
    for (int i = 0; i < numSteps; ++i) {
      newSteps.add(steps[rng.nextInt(steps.length)]);
    }
    return new DanceBeginMessage(newSteps, timeoutMillis, round);
  }

  private static SecureRandom createSecureRandom() {
    try {
      return SecureRandom.getInstance("SHA1PRNG");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to get an RNG", e);
    }
  }
}
