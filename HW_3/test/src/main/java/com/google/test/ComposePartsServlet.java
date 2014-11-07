package com.google.test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsFileMetadata;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletInputStream;

public class ComposePartsServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException
    {
        String fname = req.getParameter("filename");
        int nparts = Integer.valueOf(req.getParameter("count")); // upper limit: 32
        GcsFilename dst = new GcsFilename(Bucket, fname);

        ArrayList<String> partnames = new ArrayList<String>(nparts);
        for (int i = 0; i < nparts; i++) {
            partnames.add(fname + String.valueOf(i));
        }
        // all names of previous parts are sequenced

        gcs.compose(partnames, dst);
        // composition done!

        // delete all the part files, and their corresponding datastore entities
        for (int i = 0; i < nparts; i++) {
            gcs.delete(new GcsFilename(Bucket, partnames.get(i)));
            datastore.delete(KeyFactory.createKey("MyFile", partnames.get(i)));
        }

        Entity dse = new Entity("MyFile", fname);
        dse.setProperty("size", gcs.getMetadata(dst).getLength());
        datastore.put(dse);

        resp.getWriter().println(dst + " is created.");
    }

    private final String Bucket = "qualified-cacao-745.appspot.com";
    private final GcsService gcs = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
}