package com.google.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.*;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;


public class Remove extends HttpServlet {
    private DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    private MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private GcsService gcsService = GcsServiceFactory.createGcsService();
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	 	
    	String str = req.getParameter("filekey");
    	
    	Filter filter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, KeyFactory.createKey("MyFile", str));
    	
    	Query q = new Query("MyFile").setFilter(filter);
    	
    	PreparedQuery pq = datastoreService.prepare(q);
    	
    	Entity en = pq.asSingleEntity();
    	
    				
    	
    	if(en != null)
    	{
    		res.getWriter().println("This key exist, and its appid is" + en.getAppId());
    		
    		/////////////////////First, delete it in GCS///////////////////////
			GcsFilename gcsName = new GcsFilename("qualified-cacao-745.appspot.com", en.getProperty("GCSkey").toString());
			
    		boolean deletedGCS = gcsService.delete(gcsName);
    		
    		if(deletedGCS)
    		{
    			res.getWriter().println("deleted in GCS");
    		}
    		else
    		{
    			res.getWriter().println("Something wrong, GCS didn't delet the file");
    		}
    		
    		//////////////////////////Then, delete it in Datastore/////////////////////
    		Key datastoreKey = KeyFactory.createKey("MyFile", str);
    		datastoreService.delete(datastoreKey);
    		
    		res.getWriter().println("deleted in Datastore");
    		
    		
    		
    		//////////////////////////Then, delete it in BlobStore/////////////////////
    		
    		
    		//Problem: blobstore key is different than str, or gcs key
    		/*
    		BlobKey blobKey = new BlobKey(str);
    		blobstoreService.delete(blobKey);
    		
    		res.getWriter().println("deleted in Blobstore");
    		*/
    		
    		
    		
    		
    		
    		//////////////////////////////Finally, delete it in Memcache, if exists/////////
    		if(memcacheService.contains(str))
    		{
    			boolean deletedMemcache = memcacheService.delete(str);
    			if(deletedMemcache)
        			res.getWriter().println("deleted in Memcache");
    			else
        			res.getWriter().println("Something wrong, failed delete the file in Memcache");
       		}
    		else
    		{
    			res.getWriter().println("not in Memcache");
    		}
    	}
    	else
    	{
    		res.getWriter().println("there is no file with this key");
    	}

		
    	
    }
}

