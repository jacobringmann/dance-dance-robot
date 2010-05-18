package com.google.appengine.demos.dda.shared;

import java.util.List;

/**
 * TODO(tobyr): Doc me
 *
 * @author Toby Reyelts
 */
public class GameEndMessage extends Message {
  public GameEndMessage() {
    super(Type.GAME_END);
  }
}
