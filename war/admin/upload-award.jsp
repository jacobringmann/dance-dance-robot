<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>

<%
  BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>

<html>
 <body>
  <h1>Upload award image here</h1>
  <form action="<%= blobstoreService.createUploadUrl("/admin/upload-award") %>"
        method="post" enctype="multipart/form-data">
   <input type="file" name="myFile">
   <input type="submit" value="Submit">
  </form>
 </body>
</html>
