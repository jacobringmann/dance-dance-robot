package com.google.appengine.demos.dda.server;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Arrays;
import java.util.Map;
import java.io.IOException;

import javax.jdo.Query;
import javax.jdo.PersistenceManager;

import org.toyz.litetext.FontUtils;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.Transform;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.Composite;

public class RenderAwardServlet extends HttpServlet {
  private BlobstoreService blobstoreService;
  private ImagesService imagesService;

  public void init() {
    blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    imagesService = ImagesServiceFactory.getImagesService();
  }

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PersistenceManager pm = JdoUtil.getPm();
    Query q = pm.newQuery(Award.class);
    Award award = JdoUtil.queryFirst(q, Award.class);

    int width = 800;
    int height = 800;

    String toText = req.getParameter("to");
    String forText = req.getParameter("for");
    if (toText == null || forText == null) {
      blobstoreService.serve(award.getBlobKey(), resp);
    } else {
      FontUtils fm = new FontUtils();
      fm.setFgcolor(new int[] { 56, 51, 48 });

      Image fullImage = imagesService.composite(Arrays.asList(
        ImagesServiceFactory.makeComposite(
          imagesService.applyTransform(
            ImagesServiceFactory.makeResize(width, height),
            ImagesServiceFactory.makeImageFromBlob(award.getBlobKey())),
          0, 0, 1.0f, Composite.Anchor.TOP_LEFT),
        ImagesServiceFactory.makeComposite(
          imagesService.applyTransform(
            ImagesServiceFactory.makeVerticalFlip(),
            ImagesServiceFactory.makeImage(
              fm.doRender(toText, "LucidaBright-DemiBold"))),
          50, 255, 1.0f, Composite.Anchor.TOP_CENTER),
        ImagesServiceFactory.makeComposite(
          imagesService.applyTransform(
            ImagesServiceFactory.makeVerticalFlip(),
            ImagesServiceFactory.makeImage(
              fm.doRender(forText, "LucidaBright-DemiBold"))),
          50, 390, 1.0f, Composite.Anchor.TOP_CENTER)),
        width, height, 0);

      resp.setContentType("image/png");
      resp.getOutputStream().write(fullImage.getImageData());
    }
  }
}