package com.google.appengine.demos.dda.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import com.google.appengine.demos.dda.shared.LoginResults;

/**
 * Searches for a new Game.
 *
 * @author Toby Reyelts
 */
public class FindGamePanel extends VerticalPanel {

  TextBox nameTextBox;
  Label statusText;
  GameServiceAsync gameService;

  FindGamePanel(final Main main) {
    gameService = GameService.App.getInstance();

    HorizontalPanel firstRow = new HorizontalPanel();
    add(firstRow);
    firstRow.add(new Image(Resources.instance.androidImage()));
    Label title = new Label("Dance Dance Robot!");
    title.setStyleName("title");
    firstRow.add(title);

    HorizontalPanel secondRow = new HorizontalPanel();
    add(secondRow);
    Label name = new Label("Your Name");
    name.addStyleDependentName("nameLabel");
    secondRow.add(name);
    secondRow.add(nameTextBox = new TextBox());
    Button loginButton = new Button("Join a Game");
    loginButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        statusText.setText("Looking for a game...");
        final String name = nameTextBox.getText();
        gameService.login(name, new AsyncCallback<LoginResults>() {
          public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
          }

          public void onSuccess(LoginResults results) {
            statusText.setText("");
            main.loginComplete(name, results);
          }
        });
      }
    });
    secondRow.add(loginButton);

    HorizontalPanel thirdRow = new HorizontalPanel();
    add(thirdRow);
    thirdRow.add(statusText = new Label());
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        nameTextBox.setFocus(true);          
      }
    });
  }
}
