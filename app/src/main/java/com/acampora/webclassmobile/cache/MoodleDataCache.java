package com.acampora.webclassmobile.cache;

import com.acampora.webclassmobile.data.MoodleData;

public class MoodleDataCache extends MemoryCache<MoodleData> {
    static String TAG = "MDC";

    public static void CalculateSizes ( ) {
//        MoodleData.class.
    }

    @Override
    public long getSizeInBytes(MoodleData object) {
        return 0;
    }
}
