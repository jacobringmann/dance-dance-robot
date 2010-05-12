package com.google.appengine.demos.dda.shared;

import com.google.appengine.demos.dda.shared.values.PlayerValue;

/**
 * TODO(tobyr): Doc me
 *
 * @author Toby Reyelts
 */
public class NewPlayerMessage extends Message {

  private PlayerValue player;

  @SuppressWarnings({"UnusedDeclaration"})
  // For GWT RPC
  private NewPlayerMessage() {
    super(Type.NEW_PLAYER);
  }

  public NewPlayerMessage(PlayerValue player) {
    this();
    this.player = player;
  }

  public PlayerValue getPlayer() {
    return player;
  }
}
