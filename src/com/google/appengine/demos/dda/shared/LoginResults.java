package com.google.appengine.demos.dda.shared;

import java.io.Serializable;
import java.util.Date;

/**
 * TODO(tobyr): Doc me
 *
 * @author Toby Reyelts
 */
public class LoginResults implements Serializable {
  private String channelId;
  private Date estimatedStartTime;

  // For GWT RPC
  private LoginResults() {
  }

  public LoginResults(String channelId, Date estimatedStartTime) {
    this.channelId = channelId;
    this.estimatedStartTime = estimatedStartTime;
  }

  public String getChannelId() {
    return channelId;
  }

  public Date getEstimatedStartTime() {
    return estimatedStartTime;
  }
}
