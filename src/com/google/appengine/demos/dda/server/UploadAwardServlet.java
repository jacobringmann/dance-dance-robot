package com.google.appengine.demos.dda.server;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Map;
import java.util.Collection;
import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.jdo.Query;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobKey;

public class UploadAwardServlet extends HttpServlet {
  private BlobstoreService blobstoreService;

  public void init() {
      blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  }

  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
    BlobKey blobKey = blobs.get("myFile");

    PersistenceManager pm = JdoUtil.getPm();
    Transaction tx = pm.currentTransaction();
    try {
      tx.begin();

      Award award;
      Query q = pm.newQuery(Award.class);
      Collection c = (Collection) q.execute();
      if (c.isEmpty()) {
        award = new Award();
      } else {
	  award = (Award) c.iterator().next();
      }
      q.closeAll();

      award.setBlobKey(blobKey);
      pm.makePersistent(award);

      tx.commit();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
    }

    resp.sendRedirect("/render-award?to=User%20Name&for=Winning");
  }
}