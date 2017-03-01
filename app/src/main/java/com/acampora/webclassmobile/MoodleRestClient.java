/*
 * Created by Jonathan Acampora 2016
 * Copyright (c) 2016. All rights reserved
 *
 * Last modified 10/8/16 8:29 PM
 */

package com.acampora.webclassmobile;

import android.util.Log;

import com.acampora.webclassmobile.data.*;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;

import javax.security.auth.login.LoginException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Redd on 10/4/2016.
 */

public class MoodleRestClient {

    private static String TAG = "MOODLE_CLIENT";

    private final OkHttpClient httpClient = new OkHttpClient();
    private static ObjectMapper mapper = new ObjectMapper();

    public String getUserToken (String siteurl, String username, String password, String service, boolean retry) throws LoginException {

        String loginurl = siteurl + "login/token.php";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", username)
                .addFormDataPart("password", password)
                .addFormDataPart("service", service)
                .build();

        Request request = new Request.Builder()
                .url(loginurl)
                .post(requestBody)
                .addHeader("content-type", "multipart/form-data")
//                    .addHeader("cache-control", "no-cache")
                .build();

        byte[] responseBytes = null;
        try {
            Response response = httpClient.newCall(request).execute();
            if (response != null) {
                ResponseBody responseBody = response.body();
                responseBytes = responseBody.bytes();
                LoginResponse loginResponse = mapper.readValue(responseBytes, LoginResponse.class);
                if (loginResponse != null) {
                    if (loginResponse.token != null)
                        return loginResponse.token; // Best case, successful login
                    if (loginResponse.error != null) {
                        if (retry && loginResponse.errorcode == "requirecorrectaccess") {
                            siteurl = siteurl.replace("https://", "https://www.").replace("http://", "http://www."); // Somebody forgot the prefix
                            return this.getUserToken(siteurl, username, password, service, false);
                        } else { // Any other errors are server side
                            throw new LoginException(loginResponse.error);
                        }
                    } else {
                        throw new LoginException("invalidaccount");
                    }
                }
            }
        } catch (JsonMappingException e) {
            Log.d("TESTING", Arrays.toString(responseBytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // All fail cases end here
    }

    public MultipartBody.Builder GetRequestObjectBuilder(String token, String wsfunction) {
        return new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("wstoken", token)
            .addFormDataPart("wsfunction", wsfunction);
    }

    public ResponseBody ExecuteWebServiceRequest (String siteurl, RequestBody requestBody)  {
            final String serviceRequest = "webservice/rest/server.php?moodlewsrestformat=json";

            Request request = new Request.Builder()
                    .url(siteurl + serviceRequest)
                    .post(requestBody)
                    .addHeader("content-type", "multipart/form-data")
//                    .addHeader("cache-control", "no-cache")
                    .build();

            try {
                Response response = httpClient.newCall(request).execute();

//                if (response.code() == 404)
                return response.body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
    }
}
