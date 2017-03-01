/*
 * Created by Jonathan Acampora 2016
 * Copyright (c) 2016. All rights reserved
 *
 * Last modified 10/6/16 4:00 PM
 */

package com.acampora.webclassmobile.data;

/**
 * Created by Redd on 10/6/2016.
 */

public class CourseSection extends MoodleData {
    public int id;
    public String name;
    public int visible;
    public String summary;
    public int summaryformat;

    public SectionModule[] modules;
}
