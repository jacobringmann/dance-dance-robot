package com.google.appengine.demos.dda.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources for the application.
 *
 * @author Toby Reyelts
 */
public interface Resources extends ClientBundle {
  public static final Resources instance = GWT.create(Resources.class);

  @Source("images/android.png")
  public ImageResource androidImage();

  @Source("images/android_head.png")
  public ImageResource androidImageHead();

  @Source("images/android_left_arm.png")
  public ImageResource androidImageLeftArm();

  @Source("images/android_left_leg.png")
  public ImageResource androidImageLeftLeg();

  @Source("images/android_right_arm.png")
  public ImageResource androidImageRightArm();

  @Source("images/android_right_leg.png")
  public ImageResource androidImageRightLeg();

  @Source("images/android_sad.png")
  public ImageResource androidImageSad();

  @Source("images/android_large.png")
  public ImageResource androidLargeImage();

  @Source("images/android_head_large.png")
  public ImageResource androidLargeImageHead();

  @Source("images/android_left_arm_large.png")
  public ImageResource androidLargeImageLeftArm();

  @Source("images/android_left_leg_large.png")
  public ImageResource androidLargeImageLeftLeg();

  @Source("images/android_right_arm_large.png")
  public ImageResource androidLargeImageRightArm();

  @Source("images/android_right_leg_large.png")
  public ImageResource androidLargeImageRightLeg();

  @Source("images/android_sad_large.png")
  public ImageResource androidLargeImageSad();
}
