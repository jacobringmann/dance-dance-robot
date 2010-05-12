package com.google.appengine.demos.dda.shared;

import java.util.List;

/**
 * TODO(tobyr): Doc me
 *
 * @author Toby Reyelts
 */
public class DanceBeginMessage extends Message {
  private List<Step> steps;
  private int timeoutMillis;
  private int round;

  @SuppressWarnings({"UnusedDeclaration"})
  // For GWT RPC
  private DanceBeginMessage() {
    super(Type.DANCE_BEGIN);
  }

  public DanceBeginMessage(List<Step> steps, int timeoutMillis, int round) {
    super(Type.DANCE_BEGIN);
    this.steps = steps;
    this.timeoutMillis = timeoutMillis;
    this.round = round;
  }

  public List<Step> getSteps() {
    return steps;
  }

  public int getTimeoutMillis() {
    return timeoutMillis;
  }

  public int getRound() {
    return round;
  }
}
