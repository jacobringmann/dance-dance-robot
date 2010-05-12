package com.google.appengine.demos.dda.server.tasks;

import com.google.appengine.demos.dda.server.Game;
import com.google.appengine.demos.dda.server.Player;
import com.google.appengine.demos.dda.server.PushServer;
import com.google.appengine.demos.dda.shared.NewPlayerMessage;
import com.newatlanta.appengine.taskqueue.Deferred;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
* TODO: Doc me.
*
* @author Toby
*/
public class SendNewPlayer implements Deferred.Deferrable {
  private Player newPlayer;
  private Game game;
  public SendNewPlayer(Player newPlayer, Game game) {
    this.newPlayer = newPlayer;
    this.game = game;
  }
  public void doTask() throws ServletException, IOException {
    List<Player> players = new ArrayList<Player>(game.getPlayers());
    players.remove(newPlayer);
    PushServer.sendMessage(players, new NewPlayerMessage(newPlayer.toValue()));
  }
}
