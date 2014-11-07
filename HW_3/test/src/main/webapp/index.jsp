<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.UploadOptions" %>


<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    UploadOptions op = UploadOptions.Builder.withGoogleStorageBucketName("qualified-cacao-745.appspot.com");
%>


<html>
    <head>
        <title>HW_3 Test</title>
    </head>
    <body>
    
    	<h2>Basic functions</h2>
    	
    	
    	<h4>upload file</h4>
        <form action="<%= blobstoreService.createUploadUrl("/upload", op) %>" method="post" enctype="multipart/form-data">
            <input type="text" name="foo">
            <input type="file" name="myFile[]" multiple>
            <input type="submit" value="Submit">
        </form>
        
            
             
        <h4>list all the files</h4>
        <a href = "/list">list</a>
        
        
        <h4>check if a file exists</h4>
        <hr>
          <form action="/check" method="post">
            <div>File Key:<input type="text" name="filekey"></div>
            <div><input type="submit" value="Check This File Key"></div>
          </form>
        <hr>
        
        
        <h4>find a file and download it</h4>
        <hr>
          <form action="/download" method="post">
            <div>File Key:<input type="text" name="filekey"></div>
            <div><input type="submit" value="Find(key)"></div>
          </form>
        <hr>
        
        
        <h4>remove a file</h4>
        <hr>
          <form action="/remove" method="post">
            <div>File Key:<input type="text" name="filekey"></div>
            <div><input type="submit" value="Remove"></div>
          </form>
        <hr>
        
        
        <h2>Extra credits</h2>
        
        <h4>Remove all cache files</h4>
        <hr>
          <form action="/removeAllCache" method="post">
            <div><input type="submit" value="Remove all cache"></div>
          </form>
        <hr>
        
        <h4>Remove all files</h4> 
        <hr>
          <form action="/removeAll" method="post">
            <div><input type="submit" value="Remove all files"></div>
          </form>
        <hr>
        
        
        
        <h4>Lookup the Cache size</h4>
        <hr>
          <form action="/cacheSize" method="post">
            <div><input type="submit" value="cacheSize"></div>
          </form>
        <hr> 
        
        <h4>Lookup the GCS size</h4>
        <hr>
          <form action="/gcsSize" method="post">
            <div><input type="submit" value="GcsSize"></div>
          </form>
        <hr> 
        
        
    </body>
</html>
