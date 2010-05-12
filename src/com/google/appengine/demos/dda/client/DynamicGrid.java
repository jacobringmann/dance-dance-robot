package com.google.appengine.demos.dda.client;

import com.google.appengine.demos.dda.shared.DanceBeginMessage;
import com.google.appengine.demos.dda.shared.GameBeginMessage;
import com.google.appengine.demos.dda.shared.NewPlayerMessage;
import com.google.appengine.demos.dda.shared.Message;
import com.google.appengine.demos.dda.shared.Step;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO(tobyr): Doc me
 *
 * @author Toby Reyelts
 */
public class DynamicGrid extends Grid {
  private int nextCount = 0;

  public DynamicGrid(int rows, int cols) {
    super(rows, cols);
  }

  @Override
  public void add(Widget widget) {
    setWidget(nextCount / numColumns, nextCount % numColumns, widget);
    nextCount++;
  }

  @Override
  public boolean remove(Widget widget) {
    Widget filler = getWidget(nextCount - 1 / numColumns, nextCount - 1 % numColumns);
    for (int row = 0; row < numRows; row++) {
      for (int column = 0; column < numColumns; column++) {
	if (getWidget(row, column) == widget) {
	    setWidget(row, column, filler);
	    nextCount--;
	    setWidget(nextCount / numColumns, nextCount % numColumns, null);
	    return true;
	}
      }
    }
    return false;
  }

  @Override
  public void clear() {
    super.clear();
    nextCount = 0;
  }
}
