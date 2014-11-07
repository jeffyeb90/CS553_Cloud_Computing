package com.google.test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.*;


public class ListRegex extends HttpServlet {
    private DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	
    	String pattern = req.getParameter("regex");
        Pattern r = Pattern.compile(pattern);
 	
		Query q = new Query("MyFile").setKeysOnly();

		PreparedQuery pq = datastoreService.prepare(q);

		res.setContentType("text/plain");
		
		res.getWriter().println("File names that match the regular exprestion " + pattern);
				
		for(Entity result : pq.asIterable())
		{
			String str = result.getKey().getName();
			Matcher m = r.matcher(str);
			
			if(m.find())
				res.getWriter().println(str);
		
		}
    }
}