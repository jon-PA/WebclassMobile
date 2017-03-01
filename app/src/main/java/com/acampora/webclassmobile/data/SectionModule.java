/*
 * Created by Jonathan Acampora 2016
 * Copyright (c) 2016. All rights reserved
 *
 * Last modified 10/8/16 10:13 PM
 */

package com.acampora.webclassmobile.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.databind.Module;

import java.io.Serializable;

/**
 * Created by Redd on 10/6/2016.
 */

public class SectionModule extends MoodleData implements Serializable {
    public int id;
    public String url;
    public String name;
    public Integer instance; // Same as the assignment ID that it corresponds to (if it has one), otherwise, is some similar, though unknown value
    public String description;
    public Integer visible;
    public String modicon;
    public String modname;
    public String modplural;
    public Integer indent;

    public ModuleContent[] contents;
}