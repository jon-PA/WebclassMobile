package com.acampora.webclassmobile;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "NavDrawerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        navigationView.setCheckedItem(0);
        MenuItem courses = navigationView.getMenu().getItem(0);
//        System.out.println("COURSES: " + courses);
        courses.setChecked(true);
    }

    private void showLoginActivity() {
        Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);

        startActivity(i);
    }

    boolean firstLoad = true;

    @Override
    protected void onResume() {
        super.onResume();

        if (!firstLoad) // TODO: This code prevents a activity switch loop, find a better solution to allow this to be run more than once
            return;

        firstLoad = false;

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

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1
            );


        Menu navMenu = ((NavigationView) findViewById(R.id.nav_view)).getMenu();
        onNavigationItemSelected(navMenu.getItem(0));
//        for (int i = 0; i < navMenu.size(); i++) {
//            MenuItem item = navMenu.getItem(i);
//            if (item.isChecked()) {
//                onNavigationItemSelected(item);
//                break;
//            }
//        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ant_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            SharedPreferences.Editor preferences = getSharedPreferences("MOODLE_DATA", MODE_PRIVATE).edit();
            if (preferences != null) {
                preferences.putString("moodle_token", null);
                preferences.apply();
            }

            MoodleSiteConnection.getCurrentConnection().logout();
            showLoginActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_courses) {
            setTitle("Courses");
            Fragment fragment = new CourseListFragment();
            displayFragment(fragment);
        } else if (id == R.id.nav_grades) {
            setTitle("Grades");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayFragment (Fragment fragment) {
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_nav_drawer, fragment)
                .commit();
    }
}
