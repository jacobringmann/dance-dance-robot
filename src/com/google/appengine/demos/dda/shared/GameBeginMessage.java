package com.google.appengine.demos.dda.shared;

import java.util.List;

/**
 * TODO(tobyr): Doc me
 *
 * @author Toby Reyelts
 */
public class GameBeginMessage extends Message {
  // NB: Don't forget to add a private no-arg constructor
  // for GWT RPC if this changes.
  public GameBeginMessage() {
    super(Type.GAME_BEGIN);
  }
}
