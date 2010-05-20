package com.google.appengine.demos.dda.client;

import java.util.List;

import com.google.appengine.demos.dda.shared.Step;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;

import com.google.appengine.demos.dda.shared.Message;
import com.google.appengine.demos.dda.shared.LoginResults;

/**
 * Provides services to the game client.
 *
 * @author Toby Reyelts
 */
@RemoteServiceRelativePath("game_service")
public interface GameService extends RemoteService {

  /**
   * Utility/Convenience class. Use GameService.App.getInstance() to access
   * static instance of GameServiceAsync
   */
  public static class App {

    private static final GameServiceAsync ourInstance = (GameServiceAsync)
        GWT.create(GameService.class);

    public static GameServiceAsync getInstance() {
      return ourInstance;
    }
  }

  /**
   * Logs a user in with the given name. Returns a handle used to
   * receive push notifications from the server.
   *
   * @param name The handle by which the user will be known.
   * @return data structure containing information required by the client.
   */
  LoginResults login(String name, Long gameId, int numRounds, int waitTime);

  /**
   * Confirms a successfully opened channel for the player in
   * this session.
   *
   * @return a list of {@code Messages} that should catch the
   * player up to the current game state.
   */
  List<Message> confirmLogin();

  /**
   * Reports a Step taken by the player for this session.
   */
  void reportStep(Step step, int score, int sequence);
}
