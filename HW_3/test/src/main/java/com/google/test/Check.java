package com.google.test;

import java.io.IOException;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import com.google.appengine.api.memcache.*;
import com.google.appengine.api.memcache.MemcacheServiceFactory;


public class Check extends HttpServlet {
    private DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    private MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
    
    
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
    		
    		//check if it's also in the memcache
    		if(memcacheService.contains(str))
    			res.getWriter().println("      it's also in the memcache.");
    				
    		
    		
    	}
    	else
    	{
    		res.getWriter().println("there is no file with this key");
    	}

		
    	
    }
}

