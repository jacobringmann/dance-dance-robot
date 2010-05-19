package com.google.appengine.demos.dda.client;

import java.util.List;

import com.google.appengine.demos.dda.shared.Step;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.google.appengine.demos.dda.shared.Message;
import com.google.appengine.demos.dda.shared.LoginResults;

/**
 * TODO(tobyr): Doc me
 *
 * @author Toby Reyelts
 */
public interface GameServiceAsync {

  void login(String name, AsyncCallback<LoginResults> async);
  void confirmLogin(AsyncCallback<List<Message>> async);
  void reportStep(Step step, int score, int sequence, AsyncCallback async);
}
