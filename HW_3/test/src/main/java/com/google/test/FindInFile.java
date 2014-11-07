package com.google.test;

import java.io.IOException;



import java.util.regex.*;

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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;


public class FindInFile extends HttpServlet {
    private DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	 	
    	String str = req.getParameter("filekey");
    	String pattern = req.getParameter("regex");
 	    	
    	
    	Filter filter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, KeyFactory.createKey("MyFile", str));
    	
    	Query q = new Query("MyFile").setFilter(filter);
    	
    	Entity en = datastoreService.prepare(q).asSingleEntity();
    	
		res.setContentType("text/plain");
    	
    	   	
    	if(en == null)
    		res.getWriter().println("Error: the file key doesn't exist.");
    	else
    	{
    	      // Create a Pattern object
    	      Pattern r = Pattern.compile(pattern);

    	      // Now create matcher object.
    	      Matcher m = r.matcher(str);
    	      if (m.find()) {
    	    	  
    	    	  res.getWriter().println("The filekey" + str + " matches the regular expression" + pattern);
    	    	 
    	      } else {
    	    	  res.getWriter().println("The filekey" + str + " doesn't match the regular expression" + pattern);
    	      }
			
    		
    	}    	
    }
}