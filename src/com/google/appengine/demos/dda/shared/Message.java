package com.google.appengine.demos.dda.shared;

import java.io.Serializable;

/**
 * TODO(tobyr): Doc me
 *
 * @author Toby Reyelts
 */
public abstract class Message implements Serializable {

  public enum Type {
    NEW_PLAYER,
    GAME_BEGIN,
    MATCH_BEGIN,
    ROUND_BEGIN,
    DANCE_BEGIN,
    STEP_OCCURRED,
  }

  private Type type;

  // For GWT RPC
  private Message() {
  }

  protected Message(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }
}

