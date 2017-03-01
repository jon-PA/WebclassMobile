package com.acampora.webclassmobile.data;

import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by Redd on 1/22/2017.
 */

public class MoodleData {

    public long _maxAge = 1800000; // 30 Minutes
    public long _created = 0;

    public long getSizeBytes ( ) {
        long sizeBytes = 0;
//        long start = System.currentTimeMillis();

        // TODO: Account for size of cached moodle data
//        for (Field field : this.getClass().getFields()) {
//            if (
//                field.getType() == boolean.class ||
//                field.getType() == byte.class ||
//                field.getType() == char.class ||
//                field.getType() == short.class ||
//                field.getType() == int.class ||
//                field.getType() == float.class ||
//                field.getType() == Integer.class ||
//                field.getType() == Float.class) {
//                sizeBytes += 4;
//            } else if (
//                field.getType() == double.class ||
//                field.getType() == long.class ||
//                field.getType() == Double.class ||
//                field.getType() == Long.class) {
//                sizeBytes += 8;
//            }
//        }

//        Log.d("SIZE", "SIZE [" + this.getClass().getName() + "]: " + sizeBytes);
//        Log.d("REFLECTION", "Time: " + (System.currentTimeMillis() - start) + " ms");

        return sizeBytes;
    }
}