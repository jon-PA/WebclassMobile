package com.acampora.webclassmobile.cache;

import android.graphics.Bitmap;

public class BitmapMemoryCache extends MemoryCache<Bitmap> {

    public long getSizeInBytes(Bitmap bitmap) {
        if(bitmap==null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

}