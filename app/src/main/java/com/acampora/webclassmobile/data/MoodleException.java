/*
 * Created by Jonathan Acampora 2016
 * Copyright (c) 2016. All rights reserved
 *
 * Last modified 10/8/16 3:41 PM
 */

package com.acampora.webclassmobile.data;

/**
 * Created by Redd on 10/8/2016.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class MoodleException extends MoodleData {
    @JsonIgnoreProperties (ignoreUnknown = true)

    public String exception;
    public String errorcode;
    public String message;
}
