package com.google.appengine.demos.dda.server;

import javax.jdo.annotations.*;
import java.io.Serializable;

import com.google.appengine.api.blobstore.BlobKey;

@PersistenceCapable(identityType= IdentityType.APPLICATION)
public class Award implements Serializable {

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
  private String key;

  @Persistent
  private BlobKey blobKey;

  public String getKey() {
    return key;
  }
  
  public BlobKey getBlobKey() {
    return blobKey;
  }

  public void setBlobKey(BlobKey blobKey) {
    this.blobKey = blobKey;
  }
}
