/*
 * Created by Jonathan Acampora 2016
 * Copyright (c) 2016. All rights reserved
 *
 * Last modified 10/8/16 8:25 PM
 */

package com.acampora.webclassmobile.data;

/**
 * Created by Redd on 10/8/2016.
 */

public class LoginResponse extends MoodleData {
    public String token;

    public String error;
    public String errorcode;
    public String stacktrace;
    public String debuginfo;
    public String reproductionlink;
}
