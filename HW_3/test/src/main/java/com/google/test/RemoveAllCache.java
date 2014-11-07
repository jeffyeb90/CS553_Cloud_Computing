package com.google.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.memcache.*;


public class RemoveAllCache extends HttpServlet {
    private MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	 	
    	memcacheService.clearAll();
    	
		res.setContentType("text/plain");
    	
    	res.getWriter().println("Cache is all cleared");
   	
    }
}

