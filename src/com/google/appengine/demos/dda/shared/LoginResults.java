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
  private Long gameId;

  // For GWT RPC
  private LoginResults() {
  }

  public LoginResults(Long gameId, String channelId, Date estimatedStartTime) {
    this.gameId = gameId;
    this.channelId = channelId;
    this.estimatedStartTime = estimatedStartTime;
  }

  public Long getGameId() {
    return gameId;
  }

  public String getChannelId() {
    return channelId;
  }

  public Date getEstimatedStartTime() {
    return estimatedStartTime;
  }
}
