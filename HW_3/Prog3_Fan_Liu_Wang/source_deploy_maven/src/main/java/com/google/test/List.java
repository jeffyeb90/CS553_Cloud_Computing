package com.google.test;

import java.io.IOException;
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


public class List extends HttpServlet {
    private DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	 	
		Query q = new Query("MyFile").setKeysOnly();


		PreparedQuery pq = datastoreService.prepare(q);
		
		int i = 1;
		for(Entity result : pq.asIterable())
		{
			res.getWriter().println(result.getKey().getName());
			i++;
		}
    }
}

