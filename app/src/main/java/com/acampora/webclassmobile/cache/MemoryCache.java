package com.acampora.webclassmobile.cache;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

public abstract class MemoryCache<T> {

    private static final String TAG = "MemoryCache";
    private Map<String, T> cache=Collections.synchronizedMap(
            new LinkedHashMap<String, T>(10,1.5f,true));//Last argument true for LRU ordering
    private long size=0;//current allocated size
    private long limit=1000000;//max memory in bytes

    public MemoryCache(){
        //use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory()/4);
    }
    
    public void setLimit(long new_limit){
        limit=new_limit;
        Log.i(TAG, "MemoryCache will use up to "+limit/1024./1024.+"MB");
    }

    public T get(String id){
        try{
            if(!cache.containsKey(id))
                return null;
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78 
            return cache.get(id);
        }catch(NullPointerException ex){
            ex.printStackTrace();
            return null;
        }
    }

    public void put(String id, T item){
        try{
            if(cache.containsKey(id))
                size-=getSizeInBytes(cache.get(id));
            cache.put(id, item);
            size+=getSizeInBytes(item);
            checkSize();
        }catch(Throwable th){
            th.printStackTrace();
        }
    }
    
    private void checkSize() {
        Log.i(TAG, "cache size="+size+" length="+cache.size());
        if(size>limit){
            Iterator<Entry<String, T>> iter=cache.entrySet().iterator();//least recently accessed item will be the first one iterated  
            while(iter.hasNext()){
                Entry<String, T> entry=iter.next();
                size-=getSizeInBytes(entry.getValue());
                iter.remove();
                if(size<=limit)
                    break;
            }
            Log.i(TAG, "Clean cache. New size "+cache.size());
        }
    }

    public void clear() {
        try{
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78 
            cache.clear();
            size=0;
        }catch(NullPointerException ex){
            ex.printStackTrace();
        }
    }

    public abstract long getSizeInBytes (T object);
}