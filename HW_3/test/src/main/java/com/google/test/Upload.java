package com.google.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.List;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;


import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.cloudstorage.*;



public class Upload extends HttpServlet {
	
	
	
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    private GcsService gcsService = GcsServiceFactory.createGcsService();
    private AsyncMemcacheService asynCache = MemcacheServiceFactory.getAsyncMemcacheService();
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	
		Map<String, List<FileInfo>> fileInfoArray = blobstoreService.getFileInfos(req);
		
		Iterator<FileInfo> iter = fileInfoArray.get("myFile[]").iterator();
		Iterator<FileInfo> iter2 = fileInfoArray.get("myFile[]").iterator();
		Iterator<FileInfo> iter3 = fileInfoArray.get("myFile[]").iterator();
		//Iterator<FileInfo> iter4 = fileInfoArray.get("myFile[]").iterator();
		
		while(iter.hasNext())
		{
			
			String fileName = iter.next().getFilename();
			long fileSize = iter2.next().getSize();
			String str = iter3.next().getGsObjectName();
			
			String[] cutoff = str.split("qualified-cacao-745.appspot.com/");
			String GCSkey = cutoff[1];
					
			
			Entity en = new Entity("MyFile", fileName);
			en.setProperty("size", fileSize);
			en.setProperty("GCSkey", GCSkey);
			datastoreService.put(en);			
						
			
			if (fileSize <= 10240)
			{
				res.getWriter().println("store " + fileName + " into MemCache \n\n\n\n");
				
				GcsFilename gcsName = new GcsFilename("qualified-cacao-745.appspot.com", GCSkey);
    			
						
				
    			GcsInputChannel readChannel = gcsService.openReadChannel(gcsName, 0);
    			
    			ByteBuffer value = ByteBuffer.allocate((int)fileSize);

    			readChannel.read(value);
    			
    			byte[] byteArray = value.array();
    			
    			
    			String key = fileName;
    			
    			asynCache.put(key, byteArray);
    			
			}
		} //end of while
			
        //res.sendRedirect("/serve?blob-key=" + blobKey.getKeyString());
	        		//////////this is where the URL jump from "/Upload" to "/Serve"
	    
 	
    }//end of do Post
}
