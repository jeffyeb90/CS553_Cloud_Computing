package com.google.test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletInputStream;

public class UploadServlet extends HttpServlet {
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws IOException
    {
        ServletInputStream in = req.getInputStream();
        PrintWriter back = resp.getWriter();
        byte[] ln = new byte[1<<8];
        ByteBuffer cache = ByteBuffer.allocate(1<<20 - 96);
        int nread;
        String fname;
        while ((nread = in.readLine(ln, 0, ln.length)) > 0) {
            //back.print(new String(ln, 0, nread, "UTF8"));
            if ((fname = fnameWithin(ln, 0, nread)) != null) {
                GcsOutputChannel out =
                    gcs.createOrReplace(new GcsFilename(Bucket, fname), GcsFileOptions.getDefaultInstance());
                // skip to file content
                while (in.readLine(ln, 0, ln.length) > 0 && ln[0] != (byte)'\n' && ln[0] != (byte)'\r') {
                    back.println((new String(ln, 0, 1)) + " skip a blank line");
                }
                // next line is file content
                int nwritten = 0;
                cache.rewind();
                while ((nread = in.readLine(ln, 0, ln.length)) > 0
                       && ln[0] != (byte)'\n' && ln[0] != (byte)'\r'
                       && !(ln[0]==(byte)'-' && ln[1]==(byte)'-'
                            &&ln[nread-2]==(byte)'\r' && ln[nread-1]==(byte)'\n')) {
                    //toints(ln, nread, back);
                    //back.print(new String(ln, 0, nread));
                    if (ln[nread-2]==(byte)'\r') {
                        nwritten += out.write(ByteBuffer.wrap(ln, 0, nread-2));
                        if (cache.remaining() >= nread-2)
                            cache.put(ln, 0, nread-2);
                    } else {
                        nwritten += out.write(ByteBuffer.wrap(ln, 0, nread));
                        if (cache.remaining() >= nread)
                            cache.put(ln, 0, nread);
                    }
                }// nwritten == (size of the file)
                out.close();

                if (nwritten < 1<<20 - 96) {
                    byte[] b = new byte[nwritten];
                    cache.flip();
                    cache.get(b, 0, nwritten);
                    memcache.put(fname, b);
                }

                Entity dse = new Entity("MyFile", fname);
                dse.setProperty("size", nwritten);
                datastore.put(dse);

                back.println("finished storing " + fname + "; " + nwritten + " bytes.");
            }
        }
        // done with one file
        back.flush();
        back.close();
        in.close();
    }

    // for debug purpose
    private void toints(byte[] b, int len, PrintWriter out) {
        for (int i = 0; i < len; i++) {
            out.print((new Byte(b[i])).intValue());
        }
        out.print("\n");
    }

    /*
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws IOException
    {
        BufferedReader in = req.getReader();
        PrintWriter out = resp.getWriter();
        String line;
        while ((line = in.readLine()) != null) {
            out.println(line);
        }
        out.flush();
        out.close();
        in.close();
    }
    */

    /*
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws IOException
    {
        ServletInputStream in = req.getInputStream();
        PrintWriter out = resp.getWriter();
        String line, fname;
        byte[] ln = new byte[1<<8];
        int index;
        int nread;
        while ((nread = in.readLine(ln, 0, ln.length)) > 0) {
            if ((fname = fnameWithin(ln, 0, nread)) != null) {
                out.println(fname);
                break;
            }
        }
        out.flush();
        out.close();
        in.close();
    }
    */

    private String fnameWithin(byte[] l, int off, int len)
        throws IOException
    {
        String line = new String(l, off, len, "UTF8");
        int i = line.indexOf("filename=\"");
        if (i == -1)
            return null;
        String fname = line.substring(i + 10);
        fname = fname.substring(0, fname.indexOf("\""));
        return fname;
    }

    private final GcsService gcs =
        GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
    private final MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private final String Bucket = "qualified-cacao-745.appspot.com";
}