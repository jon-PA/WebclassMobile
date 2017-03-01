/*
 * Created by Jonathan Acampora 2016
 * Copyright (c) 2016. All rights reserved
 *
 * Last modified 10/6/16 12:55 PM
 */

package com.acampora.webclassmobile.data;

/**
 * Created by Redd on 10/4/2016.
 */

public class UserCourse extends MoodleData {
    public int id;
    public String shortname;
    public String fullname;
    public Long timemodified;
    public int enrolledusercount;
    public String idnumber;
    public int visible;
    public String summary;
    public int summaryformat;
    public String format;
    public boolean showgrades;
    public String lang;
    public boolean enablecompletion;

    public CourseAssignment[] assignments;
}
