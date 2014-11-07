package com.google.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.*;


public class GcsSize extends HttpServlet {
    private DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	 	
		Query q = new Query("MyFile");


		PreparedQuery pq = datastoreService.prepare(q);
		
		long elem = 0;
		double mb = 0.0;
		
		for(Entity result : pq.asIterable())
		{
			elem += 1;
			mb += result.getProperty("size").hashCode() / 1048576.0;
	
		}
		
		res.setContentType("text/plain");

    	res.getWriter().println("The are " + elem + "files in GCS, with total size of " + mb + " MegaBytes.\n");    	
    }
}

