/*
 * Created by Jonathan Acampora 2016
 * Copyright (c) 2016. All rights reserved
 *
 * Last modified 10/8/16 8:57 PM
 */

package com.acampora.webclassmobile.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class SiteInfo extends MoodleData
{
    @JsonIgnoreProperties(ignoreUnknown = true)

    public String sitename;
    public String username;
    public String firstname;
    public String lastname;
    public String fullname;
    public String lang;
    public int userid;
    public String siteurl;
    public String userpictureurl;

    public SiteFunction[] functions;

    public int downloadfiles;
    public int uploadfiles;
    public String release;
    public String version;
    public String mobilecssurl;

    public AdvancedFeature[] advancedfeatures;

    public boolean usercanmanageownfiles;
    public long userquota;
    public long usermaxuploadfilesize;
}