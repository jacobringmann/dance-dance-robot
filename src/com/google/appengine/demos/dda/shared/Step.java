package com.google.appengine.demos.dda.shared;

import com.google.appengine.demos.dda.client.Resources;
import com.google.gwt.resources.client.ImageResource;

/**
 * TODO(tobyr): Doc me
 *
 * @author Toby Reyelts
 */
public enum Step {
  HEAD {
    public ImageResource getImage() {
      return Resources.instance.androidImageHead();
    }
    public ImageResource getLargeImage() {
      return Resources.instance.androidLargeImageHead();
    }
  },
  LEFT_LEG {
    public ImageResource getImage() {
      return Resources.instance.androidImageLeftLeg();
    }
    public ImageResource getLargeImage() {
      return Resources.instance.androidLargeImageLeftLeg();
    }
  },
  RIGHT_LEG {
    public ImageResource getImage() {
      return Resources.instance.androidImageRightLeg();
    }
    public ImageResource getLargeImage() {
      return Resources.instance.androidLargeImageRightLeg();
    }
  },
  LEFT_ARM {
    public ImageResource getImage() {
      return Resources.instance.androidImageLeftArm();
    }
    public ImageResource getLargeImage() {
      return Resources.instance.androidLargeImageLeftArm();
    }
  },
  RIGHT_ARM {
    public ImageResource getImage() {
      return Resources.instance.androidImageRightArm();
    }
    public ImageResource getLargeImage() {
      return Resources.instance.androidLargeImageRightArm();
    }
  };

  public abstract ImageResource getImage();
  public abstract ImageResource getLargeImage();
}

