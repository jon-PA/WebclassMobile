package com.acampora.webclassmobile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.acampora.webclassmobile.data.UserCourse;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.security.auth.login.LoginException;


/**
 * Use the {@link CourseListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseListFragment extends Fragment {

    private static final String TAG = "CourseListFragment";

    ListView listView;
    SwipeRefreshLayout refreshLayout;

    public CourseListFragment() {
    }

    public static CourseListFragment newInstance() {
        CourseListFragment fragment = new CourseListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_course_list, container, false);

        Activity activity = getActivity();

        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);


        listView = (ListView) rootView.findViewById(R.id.courseList);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.courseSwipeLayout);

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
        refreshCourseList();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void refreshCourseList ( ) {
        if (MoodleSiteConnection.getCurrentConnection() == null || !MoodleSiteConnection.getCurrentConnection().isValidConnection())
            return;

        new DisplayCourseTask().execute();
    }

    private void displayCourseView (UserCourse userCourse) {
        userCourse.getSizeBytes();
        Intent i = new Intent(getActivity(), ModuleListActivity.class);
        i.putExtra("courseId", userCourse.id);
        i.putExtra("courseName", userCourse.shortname);

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

            UserCourseAdapter adapter = new UserCourseAdapter(getActivity(), userCourses);
            listView.setAdapter(adapter);
            refreshLayout.setRefreshing(false);
        }
    }
}
