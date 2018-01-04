/*
 * Created by Jonathan Acampora 2016
 * Copyright (c) 2016. All rights reserved
 *
 * Last modified 10/8/16 11:25 PM
 */

package com.acampora.webclassmobile;

import android.support.design.widget.Snackbar;
import android.util.Log;

import com.acampora.webclassmobile.cache.BitmapMemoryCache;
import com.acampora.webclassmobile.cache.ImageLoader;
import com.acampora.webclassmobile.data.CourseSection;
import com.acampora.webclassmobile.data.MoodleException;
import com.acampora.webclassmobile.data.SiteFunction;
import com.acampora.webclassmobile.data.SiteInfo;
import com.acampora.webclassmobile.data.UserCourse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;

import javax.security.auth.login.LoginException;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Redd on 10/7/2016.
 */

public class MoodleSiteConnection {
    public static MoodleSiteConnection currentConnection;
    private static MoodleRestClient restClient = new MoodleRestClient();
    private static ObjectMapper mapper = new ObjectMapper();

    public String siteurl;
    public String service;

    public String token; // TODO: Please remove

    private SiteInfo siteInfo;

    static { // No support for other Moodle Sites currently, so current connection goes here
        currentConnection = new MoodleSiteConnection("https://webclass.coleman.edu/");
    }

    public static MoodleSiteConnection getCurrentConnection() {
        return currentConnection;
    }
    public BitmapMemoryCache bitmapCache;

    public static void setCurrentConnection(MoodleSiteConnection currentConnection) {
        MoodleSiteConnection.currentConnection = currentConnection;
    }

    public MoodleSiteConnection(String siteurl, String service) {
        this.siteurl = siteurl;
        this.service = service;

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public MoodleSiteConnection(String siteurl) {
        this(siteurl, "moodle_mobile_app");
    }

     // Check to see if the connection "should" be able to be utilized (login will be validated on the next ws call)
    public boolean isValidConnection ( ) {
        return (token != null && !token.equals(""));
    }

     // Use the restClient to obtain a token
    public boolean login (String username, String password) throws LoginException {
        String token;

        siteInfo = null;

        token = restClient.getUserToken(siteurl, username, password, service, true);

//        Log.d("DEBUG", token);
        if (token != null) {
            this.token = token;
            return true;
        }
        return false;
    }

    public void logout ( ) {
        token = null;
    }

    public SiteInfo fetchSiteInfo ( ) throws LoginException {
        RequestBody requestBody = restClient.GetRequestObjectBuilder(token, "core_webservice_get_site_info").build();
        ResponseBody responseBody = restClient.ExecuteWebServiceRequest(siteurl, requestBody);

        SiteInfo siteInfo = null;
        byte[] bytes = null;
        try {
//            Log.d(TAG, responseBody.string());
            bytes =  responseBody.bytes();
            siteInfo = mapper.readValue(bytes, SiteInfo.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            if (bytes != null) {
                Log.d("SITEINFO", new String(bytes));
                parseForLoginError(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseBody.close();
        return siteInfo;
    }

     // Reads a response from a ws, checks if it is an exception response, and throws if so
    public boolean parseForLoginError(byte[] bytes) throws LoginException {
        try {
            MoodleException exception = mapper.readValue(bytes, MoodleException.class);
            if (exception.errorcode.equals("invalidtoken")) {
                throw new LoginException("tokenexpired");
            }
        } catch (JsonParseException e) {
            // Malformed JSON response (possibly responded with XML?)
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // Was not an exception, so it responded with the both the wrong ws response, and something other than an error (possibly a different ws?)
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("TESTING", new String(bytes));

        return false;
    }

    public SiteInfo getSiteInfo ( ) throws LoginException { // (Sh/C)ould be used
        if (this.siteInfo == null)
            this.siteInfo = fetchSiteInfo();

        return this.siteInfo;
    }

    public boolean isSiteFunctionEnabled (String siteFunctionName) throws LoginException {
        SiteInfo siteInfo = getSiteInfo();

        for (SiteFunction siteFunction : siteInfo.functions)
            if (siteFunction.name.equals(siteFunctionName))
                return true;

        return false;
    }

    public UserCourse[] fetchUsersCourses (String userId) throws LoginException { // Should check if function is enabled, but this is currently only for webclass, so no need
        MultipartBody.Builder requestBodyBuilder = restClient.GetRequestObjectBuilder(token, "core_enrol_get_users_courses");
        requestBodyBuilder.addFormDataPart("userid", userId);
        RequestBody requestBody = requestBodyBuilder.build();
        ResponseBody responseBody = restClient.ExecuteWebServiceRequest(siteurl, requestBody);

        UserCourse[] userCourses = null;
        byte[] bytes = null;
        try {
//            Log.d(TAG, responseBody.string());
            bytes =  responseBody.bytes();
            userCourses = mapper.readValue(bytes, UserCourse[].class);
        } catch (JsonProcessingException e) {
            if (bytes != null) {
                parseForLoginError(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseBody.close();
        return userCourses;
    }

    public CourseSection[] fetchCourseContents (String courseid) throws LoginException { // Should check if function is enabled, but this is currently only for webclass, so no need
        MultipartBody.Builder requestBodyBuilder = restClient.GetRequestObjectBuilder(token, "core_course_get_contents");
        requestBodyBuilder.addFormDataPart("courseid", courseid);
        RequestBody requestBody = requestBodyBuilder.build();
        ResponseBody responseBody = restClient.ExecuteWebServiceRequest(siteurl, requestBody);

        CourseSection[] courseContents = null;
        byte[] bytes = null;
        String string = null;
        try {
//            Log.d(TAG, responseBody.string());
            bytes =  responseBody.bytes();
            courseContents = mapper.readValue(bytes, CourseSection[].class);
            string = new String(bytes, "UTF-8");
        } catch (JsonProcessingException e) {
            if (bytes != null) {
                parseForLoginError(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseBody.close();
        return courseContents;
    }
}
