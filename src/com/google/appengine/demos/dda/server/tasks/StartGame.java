package com.google.appengine.demos.dda.server.tasks;

import com.google.appengine.demos.dda.server.Game;
import com.google.appengine.demos.dda.server.GameServiceImpl;
import com.google.appengine.demos.dda.server.JdoUtil;
import com.google.appengine.demos.dda.server.PushServer;
import com.google.appengine.demos.dda.shared.GameBeginMessage;
import com.newatlanta.appengine.taskqueue.Deferred;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.servlet.ServletException;
import java.io.IOException;

/**
* TODO: Doc me.
*
* @author Toby
*/
public class StartGame implements Deferred.Deferrable {
  private final long gameId;
  public StartGame(long gameId) {
    this.gameId = gameId;
  }

  public void doTask() throws ServletException, IOException {
    PersistenceManager pm = JdoUtil.getPm();
    Transaction tx = pm.currentTransaction();

    Game game;
    try {
      tx.begin();
      Query q = pm.newQuery(Game.class, "id == " + gameId);
      game = JdoUtil.queryFirst(q, Game.class);
      if (game.getState() == Game.State.IN_PROGRESS) {
        // Already started, nothing to do.
        return;
      }
      game.setState(Game.State.IN_PROGRESS);
      tx.commit();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
    }

    PushServer.sendMessage(game.getPlayers(), new GameBeginMessage());

    Deferred.defer(new SendDanceBegin(game, SendDanceBegin.createRandomDance(0)), 
        GameServiceImpl.getTaskOptions().countdownMillis(1000));
  }
}
