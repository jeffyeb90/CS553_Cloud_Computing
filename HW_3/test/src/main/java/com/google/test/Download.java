package com.google.test;

import java.io.*;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.*;



public class Download extends HttpServlet {
    private DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    private MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    
    
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	 	
    	String str = req.getParameter("filekey");
    	
    	Filter filter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, KeyFactory.createKey("MyFile", str));
    	
    	Query q = new Query("MyFile").setFilter(filter).setKeysOnly();
    	
    	PreparedQuery pq = datastoreService.prepare(q);
    	
    	Entity en = pq.asSingleEntity();
    	
		res.setContentType("text/plain");

    	
    	
    	if(en != null)
    	{
			String encodedFilename = URLEncoder.encode(str, "utf-8");
			encodedFilename.replaceAll("\\+", "%20");
			res.setContentType("application/octet-stream");

			res.addHeader("Content-Disposition", "attachment; filename*=utf-8''" + encodedFilename );
    		//This key exist, now check if it's also in memcache
    		
	        Object value = memcacheService.get(str);

    		if(value != null)
    		{

    	        byte[] data = (byte []) value;
    	        // data is ready for download

    	        //res.setContentType("application/octet-stream");
    	        ServletOutputStream out = res.getOutputStream();
    	        out.write(data);
    	        out.flush();
    	        out.close();		
    		}
    		else
    		{
    			//not in the cache, download from GCS
    			String gcsName = "/gs/qualified-cacao-745.appspot.com/" + str;
 			   			
    			BlobKey blobKey = blobstoreService.createGsBlobKey(gcsName);
    			
    			blobstoreService.serve(blobKey, res);
    		}
      	}
    	else
    	{
    		res.getWriter().println("there is no file with this key");
    	}

		
    	
    }
}
