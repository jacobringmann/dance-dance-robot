package com.google.appengine.demos.dda.client;

import com.google.appengine.demos.dda.shared.Step;

/**
 * Maps coordinates of the main android image to dance Steps.
 *
 * @author tobyr@google.com
 */
public class ImageMap {

  private static final int armWidth = 42;
  private static final int armHeight = 127;

  private static final int leftArmX = 298;
  private static final int leftArmY = 150;

  private static final int rightArmX = 65;
  private static final int rightArmY = 150;

  private static final int legWidth = 46;
  private static final int legHeight = 65;

  private static final int leftLegX = 213;
  private static final int leftLegY = 307;

  private static final int rightLegX = 146;
  private static final int rightLegY = 307;

  private static final int headCenterX = 203;
  private static final int headCenterY = 155;
  private static final int headRadius = 90;

  /**
   * Returns the corresponding dance Step for the image coordinates
   * or null if no Step matches.
   *
   * @param x
   * @param y
   * @return
   */
  public static Step getStep(int x, int y) {
    if (inRectBounds(x, y, leftArmX, leftArmY, armWidth, armHeight)) {
      return Step.LEFT_ARM;
    } else if (inRectBounds(x, y, rightArmX, rightArmY, armWidth, armHeight)) {
      return Step.RIGHT_ARM;
    } else if (inRectBounds(x, y, leftLegX, leftLegY, legWidth, legHeight)) {
      return Step.LEFT_LEG;
    } else if (inRectBounds(x, y, rightLegX, rightLegY, legWidth, legHeight)) {
      return Step.RIGHT_LEG;
    } else if ((distanceBetween(x, y, headCenterX, headCenterY) < headRadius) && y <= headCenterY) {
      return Step.HEAD;
    }
    return null;
  }

  private static boolean inRectBounds(int x, int y, int rectX, int rectY, int rectWidth, int rectHeight) {
    return x >= rectX && x <= rectX + rectWidth && y >= rectY && y <= rectY + rectHeight;
  }

  private static long distanceBetween(int x1, int y1, int x2, int y2) {
    return (int) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
  }
}
