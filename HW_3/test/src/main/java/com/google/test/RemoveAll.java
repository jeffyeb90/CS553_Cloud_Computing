package com.google.test;

import java.io.IOException;
import java.util.*;
import java.util.List;

/*
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
*/
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;


public class RemoveAll extends HttpServlet {
    private DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    private GcsService gcsService = GcsServiceFactory.createGcsService();
    private MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
    
    
    
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	 	
  		List<Key> keyArray = new ArrayList<Key>();

    	
    	/////////////////First, delete all GCS files///////////
		Query q = new Query("MyFile");

		PreparedQuery pq = datastoreService.prepare(q);
		
		res.setContentType("text/plain");
		
		for(Entity result : pq.asIterable())
		{
			//res.getWriter().println("the gcs key is " + result.getKey().getName());
			
			
			GcsFilename gcsName = new GcsFilename("qualified-cacao-745.appspot.com", result.getKey().getName());
			
			gcsService.delete(gcsName);
	
			keyArray.add(result.getKey());
		}
		
		res.getWriter().println("GCS files deleted");
	
		
		////////////////////Then, delete all Datastore metadata//////////
		
		
		datastoreService.delete(keyArray);
		res.getWriter().println("datastore metadata all deleted");

		
		/////////////////////Finally, clear memcache///////////////////////
    	
		memcacheService.clearAll();
		res.getWriter().println("memcache cleared");

    }
}

