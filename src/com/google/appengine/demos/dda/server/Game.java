package com.google.appengine.demos.dda.server;

import javax.jdo.annotations.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TODO: Doc me.
 *
 * @author Toby
 */
@PersistenceCapable(identityType= IdentityType.APPLICATION)
public class Game implements Serializable {

  public enum State {
    /**
     * Only one Game in the system can be in the NEW state
     * at any given time.
     */
    NEW,
    IN_PROGRESS,
    COMPLETE
  }

  @PrimaryKey
  private Long id;

  @Persistent
  private State state;

  @Persistent
  private List<Player> players;

  @Persistent
  private Date timeCreated;

  @Persistent
  private Integer numRounds;

  /**
   * Creates a new Game in the NEW state.
   */
  public Game(Long id) {
    this.id = id;
    state = State.NEW;
    timeCreated = new Date(System.currentTimeMillis());
    players = new ArrayList<Player>();
  }

  public Long getId() {
    return id;
  }
  
  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }
  
  public List<Player> getPlayers() {
    return players;
  }

  public Date getTimeCreated() {
    return timeCreated;
  }

  public Integer getNumRounds() {
    return numRounds;
  }

  public void setNumRounds(Integer numRounds) {
    this.numRounds = numRounds;
  }
}
