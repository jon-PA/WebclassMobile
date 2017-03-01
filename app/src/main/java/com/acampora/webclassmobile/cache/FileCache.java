package com.acampora.webclassmobile.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import android.Manifest;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class FileCache {
    
    private File cacheDir;
    
    public FileCache(Context context){

        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"LazyList");
//            try {
//                cacheDir.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();

//        MediaScannerConnection.scanFile(context, new String[] { cacheDir.toString() }, null, // Fix for Nexus Devices indexing issue?
//                new MediaScannerConnection.OnScanCompletedListener() {
//                    public void onScanCompleted(String path, Uri uri) {
//                        Log.i("ExternalStorage", "Scanned " + path + ":");
//                        Log.i("ExternalStorage", "-> uri=" + uri);
//                    }
//                });
//        clear();
    }
    
    public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
//        String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;
    }

    public void putFile (String url, File f) {
        try {
            File file = getFile(url); // Write To
            OutputStream os = new FileOutputStream(f); // Out from what we are putting
            InputStream is = new FileInputStream (file);

            Utils.CopyStream(is, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}