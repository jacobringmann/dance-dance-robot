package com.google.appengine.demos.dda.shared.values;

import java.io.Serializable;

/**
 * TODO: Doc me.
 *
 * @author Toby
 */
public class PlayerValue implements Serializable {
  private String key;
  private String name;

  // For GWT RPC
  @SuppressWarnings({"UnusedDeclaration"})
  private PlayerValue() {
  }

  public PlayerValue(String key, String name) {
    this.key = key;
    this.name = name;
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }
}
