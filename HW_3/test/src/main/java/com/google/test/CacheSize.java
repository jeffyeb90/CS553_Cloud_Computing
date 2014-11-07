package com.google.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.memcache.Stats;

public class CacheSize extends HttpServlet {
    private MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
    
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	 	
    	Stats stat = memcacheService.getStatistics();
    		
    	long elem = stat.getItemCount();
    	double mb = stat.getTotalItemBytes() / 1048576.0;
    	
		res.setContentType("text/plain");
   	
    	res.getWriter().println("The are " + elem + "files in the cache, with total size of " + mb + " MegaBytes.\n");
    	
    	//res.encodeRedirectURL(url)
    	
    }
}

