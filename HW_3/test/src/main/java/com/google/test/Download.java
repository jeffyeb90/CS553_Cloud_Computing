package com.google.test;

import java.io.*;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    	
    	Query q = new Query("MyFile").setFilter(filter);
    	
    	PreparedQuery pq = datastoreService.prepare(q);
    	
    	Entity en = pq.asSingleEntity();
    	
    	
    	
    	if(en != null)
    	{
    		//This key exist, now check if it's also in memcache
    		
    		if(memcacheService.contains(str))
    		{
    			//it's also in the memcache, download from cache
    			
    			
        		/////////////////////////////////////////////////////////////////////////////
        		/*
        		
        		String contextPath = getServletContext().getRealPath(File.separator);
    			File file = new File(contextPath + str);
    			
    			//res.getWriter().println("size = " + en.getProperty("size").hashCode()+1 + " bytes");
    			
    			res.setContentType("application/octet-stream");
    			
    			res.setContentLength(en.getProperty("size").hashCode());
    			
    			
    			
    			String headerKey = "Content-Disposition";
    			String headerValue = String.format("attachment; filename=\"%s\"", file.getName());
    			res.setHeader(headerKey, headerValue);
    			
    			// obtains response's output stream
    			
    			
    			FileInputStream inStream = new FileInputStream(file);
    			OutputStream outStream = res.getOutputStream();
    		
    			
    			byte[] buffer = new byte[1024];
    			int bytesRead = -1;
    			
    			while ((bytesRead = inStream.read(buffer)) != -1) 
    			{
    					outStream.write(buffer, 0, bytesRead);
    			}
    			
    			
    			inStream.close();
    			outStream.close();
    			
    			*/
        		
    			////////////////////////////////////////////////////////////////////////////////
    		    		   			
    		}
    		else
    		{
    			//not in the cache, download from GCS
    			String gcsName = "/gs/qualified-cacao-745.appspot.com/" + en.getProperty("GCSkey");
    			
    			//res.getWriter().println(gcsName);
    			
    			BlobKey blobKey = blobstoreService.createGsBlobKey(gcsName);
    			
    			blobstoreService.serve(blobKey, res);
    			
    			
    			//BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);

    			String encodedFilename = URLEncoder.encode(str, "utf-8");
    			encodedFilename.replaceAll("\\+", "%20");
    			res.setContentType("application/octet-stream");

    			res.addHeader("Content-Disposition", "attachment; filename*=utf-8''" + encodedFilename );

    		}
      	}
    	else
    	{
    		res.getWriter().println("there is no file with this key");
    	}

		
    	
    }
}
