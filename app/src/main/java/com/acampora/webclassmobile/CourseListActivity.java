/*
 * Created by Jonathan Acampora 2016
 * Copyright (c) 2016. All rights reserved
 *
 * Last modified 10/8/16 11:40 PM
 */

package com.acampora.webclassmobile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.acampora.webclassmobile.data.UserCourse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.security.auth.login.LoginException;

public class CourseListActivity extends AppCompatActivity {

    private static final String TAG = "CourseViewActivity";

    ListView listView;
    SwipeRefreshLayout refreshLayout;

    public MoodleRestClient restClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState);
        setContentView(R.layout.activity_courses_view);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        listView = (ListView) findViewById(R.id.listView);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCourseList ( );
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserCourse itemAtPosition = (UserCourse) listView.getItemAtPosition(position);
//                Log.d(TAG, itemAtPosition.fullname);
                displayCourseView (itemAtPosition);
            }
        });

    }

    private void showLoginActivity() {
        Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);

        startActivity(i);
    }

    private void refreshCourseList ( ) {
        new DisplayCourseTask().execute();
    }

    private void displayCourseView (UserCourse userCourse) {
        userCourse.getSizeBytes();
        Intent i = new Intent(getApplicationContext(), ModuleListActivity.class);
        i.putExtra("courseId", userCourse.id);
        i.putExtra("courseName", userCourse.shortname);

        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("MOODLE_DATA", MODE_PRIVATE);
        if (preferences != null) {
            String loginToken = preferences.getString("moodle_token", null);
            if (loginToken != null) {
                Log.d(TAG, "Shared preferences login token: " + loginToken);
                MoodleSiteConnection.currentConnection.token = loginToken;
            }
        }

        if (MoodleSiteConnection.getCurrentConnection() == null || !MoodleSiteConnection.getCurrentConnection().isValidConnection())
            showLoginActivity();
        else
            refreshCourseList ( );

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);


    }

    public void onLogoutClick(View view) {

        SharedPreferences.Editor preferences = getSharedPreferences("MOODLE_DATA", MODE_PRIVATE).edit();
        if (preferences != null) {
            preferences.putString("moodle_token", null);
            preferences.apply();
        }

        MoodleSiteConnection.getCurrentConnection().logout();
        Intent i = new Intent(this, WelcomeActivity.class);
        startActivity(i);
    }

    public class UserCourseAdapter extends ArrayAdapter<UserCourse> {
        public UserCourseAdapter(Context context, UserCourse[] users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            UserCourse course = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.primaryTextView);
            TextView tvHome = (TextView) convertView.findViewById(R.id.secondaryTextView);
            // Populate the data into the template view using the data object
            tvName.setText(course.fullname);
            tvHome.setText(course.summary);
            // Return the completed view to render on screen
            return convertView;
        }


    }

    class DisplayCourseTask extends AsyncTask<Void, Void, UserCourse[]> {
        ObjectMapper mapper = new ObjectMapper();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refreshLayout.setRefreshing(true);
        }

        @Override
        protected UserCourse[] doInBackground(Void... params) {
            try {
                return MoodleSiteConnection.currentConnection.fetchUsersCourses(MoodleSiteConnection.currentConnection.getSiteInfo().userid + "");
            } catch (LoginException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(UserCourse[] userCourses) {

            UserCourseAdapter adapter = new UserCourseAdapter(getApplicationContext(), userCourses);
            listView.setAdapter(adapter);
            refreshLayout.setRefreshing(false);
        }
    }
}
