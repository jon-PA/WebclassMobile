/*
 * Created by Jonathan Acampora 2016
 * Copyright (c) 2016. All rights reserved
 *
 * Last modified 10/8/16 8:36 PM
 */

package com.acampora.webclassmobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WelcomeActivity extends AppCompatActivity {

    private View mContentView;
    private Button openLoginActivityButton;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        openLoginActivityButton = (Button) findViewById(R.id.openLoginActivityButton);
        setFullscreen ( );
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullscreen();

        if (MoodleSiteConnection.getCurrentConnection() == null || !MoodleSiteConnection.getCurrentConnection().isValidConnection())
            openLoginActivityButton.setEnabled(true);
        else
            finish();

    }

    private void setFullscreen ( ) {
        mContentView = findViewById(R.id.welcomeText1);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setFullscreen ( );
            if (data.getBooleanExtra("LoginSuccess", false)) {
                Intent i2 = new Intent(this, CourseListActivity.class);
                startActivity(i2);
                finish();
            }
        }
    }

    public void openLogin(View view) {
        openLoginActivityButton.setEnabled(false);

        Intent i = new Intent(this, LoginActivity.class);
        startActivityForResult(i, 1);
//        startActivity(i);
    }

}
