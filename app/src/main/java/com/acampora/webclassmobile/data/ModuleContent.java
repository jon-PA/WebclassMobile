/*
 * Created by Jonathan Acampora 2016
 * Copyright (c) 2016. All rights reserved
 *
 * Last modified 10/8/16 10:14 PM
 */

package com.acampora.webclassmobile.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Redd on 10/6/2016.
 */

public class ModuleContent extends MoodleData {
    public String type;
    public String filename;
    public String filepath;
    public Long filesize;
    public String fileurl;
    public Long timecreated;
    public Long timemodified;
    public Integer sortorder;
    public Integer userid;
    public String author;
    public String license;
}
