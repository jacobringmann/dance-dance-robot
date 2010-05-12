package com.google.appengine.demos.dda.client;

import com.google.appengine.demos.dda.shared.Message;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async interface for {@link PushService}. This class is not used directly
 * by client code.
 *
 * @author Toby Reyelts
 */
public interface PushServiceAsync {
  void receiveMessage(AsyncCallback<Message> async);
}
