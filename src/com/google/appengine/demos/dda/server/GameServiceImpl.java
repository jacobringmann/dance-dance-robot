package com.google.appengine.demos.dda.server;

import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.demos.dda.client.GameService;
import com.google.appengine.demos.dda.server.tasks.SendNewPlayer;
import com.google.appengine.demos.dda.server.tasks.StartGame;
import com.google.appengine.demos.dda.shared.LoginResults;
import com.google.appengine.demos.dda.shared.Message;
import com.google.appengine.demos.dda.shared.NewPlayerMessage;
import com.google.appengine.demos.dda.shared.Step;
import com.google.appengine.demos.dda.shared.StepOccurredMessage;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Date;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;
import static com.newatlanta.appengine.taskqueue.Deferred.defer;

/**
 * The server-side implementation of the GWT-RPC {@link GameService}.
 *
 * @author Toby Reyelts
 */
public class GameServiceImpl extends RemoteServiceServlet
    implements GameService {

  private static final int MAX_PLAYERS = 20;
  private static final int MAX_WAIT_TIME_MILLIS = 60000;

  private static final String PLAYER = "player";
  private static final String GAME_ID = "game_id";

  public static final String CURRENT_GAME_ID = "current_game_id";
  private static final String CURRENT_GAME_NUM_PLAYERS = "num_players";

  public LoginResults login(final String name) {
    long gameId = reserveGame();
    HttpSession session = getThreadLocalRequest().getSession();
    session.setAttribute(GAME_ID, gameId);
    Game game = getGameById(gameId);

    Player player = new Player();
    player.setName(name);
    session.setAttribute(PLAYER, player);

    PersistenceManager pm = JdoUtil.getPm();
    Transaction tx = pm.currentTransaction();

    try {
      tx.begin();
      game.getPlayers().add(player);
      pm.makePersistent(game);
      tx.commit();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
    }

    defer(new SendNewPlayer(player, game), getTaskOptions());

    if (game.getPlayers().size() >= MAX_PLAYERS) {
      // We're full, start the game early.
      // TODO(tobyr) We really can't send a StartGame message until
      // after we've received a confirmed login from all users.
      // We wait for three seconds as a hack for now.
      defer(new StartGame(gameId), getTaskOptions().countdownMillis(3000));
    }

    Date estimatedStartTime = new Date(game.getTimeCreated().getTime() +
                                       MAX_WAIT_TIME_MILLIS);

    String channelId = PushServer.createChannel(player);
    return new LoginResults(channelId, estimatedStartTime);
  }

  private boolean tryCreateGame(long gameId) {
    PersistenceManager pm = JdoUtil.getPm();
    Transaction tx = pm.currentTransaction();

    try {
      tx.begin();
      Game game = new Game(gameId);
      pm.makePersistent(game);
      tx.commit();
    } catch (JDOException e) {
      e.printStackTrace();
      // NB(tobyr) Need to figure out really what exception to catch here.
      return false;
    }
    finally {
      if (tx.isActive()) {
        tx.rollback();
      }
    }
    return true;
  }

  public List<Message> confirmLogin() {
    Game game = getGameForSession();
    List<Message> messages = new ArrayList<Message>();
    for (Player player : game.getPlayers()) {
      messages.add(new NewPlayerMessage(player.toValue()));
    }
    return messages;
  }

  public void reportStep(Step step, int score) {
    StepOccurredMessage msg = new StepOccurredMessage(getPlayerForSession().toValue(), step, score);
    Game game = getGameForSession();
    PushServer.sendMessage(game.getPlayers(), msg);
  }

  private Game getGameForSession() {
    Long gameId = (Long)getThreadLocalRequest().getSession().getAttribute(GAME_ID);
    return getGameById(gameId);
  }

  private Player getPlayerForSession() {
    return (Player)getThreadLocalRequest().getSession().getAttribute(PLAYER);
  }

  public static TaskOptions getTaskOptions() {
    return url("/tasks/deferred");
  }

  private static Game getGameById(Long gameId) {
    PersistenceManager pm = JdoUtil.getPm();
    Query q = pm.newQuery(Game.class, "id == " + gameId);
    return JdoUtil.queryFirst(q, Game.class);
  }

  /**
   * Returns the largest existing game id.
   * Returns 0 if no game has ever been created.
   */
  private long queryForLargestGameId() {
    PersistenceManager pm = JdoUtil.getPm();
    Query q = pm.newQuery(Game.class);
    q.setOrdering("id descending");
    Collection results = (Collection)q.execute();
    if (results.size() == 0) {
      return 0;
    }
    return ((Game)results.iterator().next()).getId();
  }

  /**
   * Reserve a spot in a game for a player. May create a new game
   * in the process.
   * @return the id of the game which has a reserved spot
   */
  private long reserveGame() {
    // NB(tobyr) Starvation looks possible if we are always beaten out while
    // trying to join or create a new game.

    MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
    Long gameId = getCachedGameId(cache);

    if (gameId != null) {
      if (cache.increment(gameId + "-" + CURRENT_GAME_NUM_PLAYERS, 1, 0L) <= MAX_PLAYERS) {
        // There's room for another player, but the game may have already
        // started.
        PersistenceManager pm = JdoUtil.getPm();
        Transaction tx = pm.currentTransaction();
        try {
          tx.begin();
          Game game = getGameById(gameId);
          if (game.getState() == Game.State.NEW) {
            return gameId;
          }
          tx.commit();
        } finally {
          if (tx.isActive()) {
            tx.rollback();
          }
        }
      }
    }

    gameId = queryForLargestGameId() + 1;

    if (tryCreateGame(gameId)) {
      cache.put(CURRENT_GAME_ID, gameId);
      cache.put(gameId + "-" + CURRENT_GAME_NUM_PLAYERS, 1);
      defer(new StartGame(gameId), getTaskOptions().countdownMillis(MAX_WAIT_TIME_MILLIS));
      return gameId;
    }

    // We couldn't create it, so someone else must have.
    // Search for a newer game id in cache than the
    // one we just tried to create.
    gameId = findNewerGameInCache(gameId);

    // If our cache disappeared, start all over again.
    return gameId == null ? reserveGame() : gameId;
  }

  private Long getCachedGameId(MemcacheService memcache) {
    return (Long)memcache.get(CURRENT_GAME_ID);
  }

  private Long findNewerGameInCache(Long gameId) {
    MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
    Long newGameId = getCachedGameId(cache);
    while (newGameId <= gameId) {
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        //
      }
      newGameId = getCachedGameId(cache);
      if (newGameId == null) {
        return null;
      }
    }
    return newGameId;
  }
}
