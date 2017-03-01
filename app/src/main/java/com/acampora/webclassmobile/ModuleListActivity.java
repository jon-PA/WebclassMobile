/*
 * Created by Jonathan Acampora 2016
 * Copyright (c) 2016. All rights reserved
 *
 * Last modified 10/8/16 11:04 PM
 */

package com.acampora.webclassmobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;


import com.acampora.webclassmobile.cache.ImageLoader;
import com.acampora.webclassmobile.data.CourseSection;
import com.acampora.webclassmobile.data.SectionModule;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

/**
 * An activity representing a list of Modules. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ModuleDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ModuleListActivity extends AppCompatActivity {

    private static final String TAG = "ModuleListActivity";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;

    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_list);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.frameLayout);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        recyclerView = (RecyclerView) findViewById(R.id.module_list);
        assert recyclerView != null;
//        setupRecyclerView((RecyclerView) recyclerView);

        imageLoader = new ImageLoader(getApplicationContext());
        Log.d("Module List Activity!", "Creating image loader~~~");

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ModuleDisplayTask().execute();
            }
        });

        if (findViewById(R.id.module_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        new ModuleDisplayTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class ModuleDisplayTask extends AsyncTask<Void, Void, CourseSection[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refreshLayout.setRefreshing(true);
        }

        @Override
        protected CourseSection[] doInBackground(Void... params) {
            Intent i = getIntent();
            int id = i.getIntExtra("courseId", -1);
            if (id == -1)
                return null;
            String name = i.getStringExtra("courseName");
            ObjectMapper mapper = new ObjectMapper();

            CourseSection[] sections = null;
            try {
//                InputStream stream = getResources().getAssets().open("example_coursecontent.json");
//                sections = mapper.readValue(stream, CourseSection[].class);
              sections = MoodleSiteConnection.currentConnection.fetchCourseContents(id + "");
//            } catch (IOException e) {
//                e.printStackTrace();
            } catch (LoginException e) {
                e.printStackTrace();
            }
            return sections;
        }

        @Override
        protected void onPostExecute(CourseSection[] sections) {
            if (sections != null) {
                List<SectionModule> modules = new ArrayList<>();
                List<SimpleSectionedRecyclerViewAdapter.Section> adapterSections =
                        new ArrayList<>();
                int pos = 0;
                for (CourseSection section : sections) {
                    adapterSections.add(new SimpleSectionedRecyclerViewAdapter.Section(pos, section.name));
                    for (SectionModule mod : section.modules) {
                        modules.add(mod);
                        pos++;
//                    }
                    }
                }
                SimpleAdapter baseAdapter = new SimpleAdapter(modules);
                SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[adapterSections.size()];
                SimpleSectionedRecyclerViewAdapter sectionedAdapter = new
                        SimpleSectionedRecyclerViewAdapter(getApplicationContext(),R.layout.section,R.id.section_text,baseAdapter);
                sectionedAdapter.setSections(adapterSections.toArray(dummy));
                setupRecyclerView(sectionedAdapter);
            }
            Log.d(TAG, "REFRESH OVER");
            refreshLayout.setRefreshing(false);
        }
    }

    private void setupRecyclerView(SimpleSectionedRecyclerViewAdapter sectionedAdapter) {
        recyclerView.setAdapter(sectionedAdapter);
    }

    public class SimpleAdapter
            extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

        private final List<SectionModule> modules;

        public SimpleAdapter(List<SectionModule> items) {
            modules = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.module_list_content, parent, false);
            return new ViewHolder(view);
        }

        public String superTrim (String input) {
            return input.replaceAll("\\s+", " ").replaceAll("\n", "").trim();
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final SectionModule module = modules.get(position);

            holder.item = module;
//            holder.mIdView.setText(modules.get(position).id);
            holder.content.setText(superTrim (module.name));
//            Log.d("ModuleList", "[" + getMimeType(module.modicon) + "]: " + module.modicon);

//            Bitmap icon = bitmapFromUrl(module.modicon);
//            holder.icon.setImageBitmap(icon); // Will be done in a task later

            imageLoader.DisplayImage(module.modicon, holder.icon);

            holder.containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
//                        Bundle arguments = new Bundle();
//                        arguments.putParcelable("module", module);
//                        arguments.putString(ModuleDetailFragment.ARG_ITEM_ID, holder.item.name);
                        ModuleDetailFragment fragment = new ModuleDetailFragment();
                        fragment.setModule(module);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.module_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext(); // This bit doesn't work
                        Intent intent = new Intent(context, ModuleDetailActivity.class);
                        intent.putExtra("sectionModule", module);
                        try {
                            context.startActivity(intent);
                        } catch (Exception e) {
//                            Log.d(TAG, "Section Data:");
//                            Log.d(TAG, "\t" + module.modname);
//                            Log.d(TAG, "\t" + module.name);
//                            Log.d(TAG, "\t" + module.description);
//                            Log.d(TAG, "\t" + module.id);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return modules.size();
        }

        // This holds reference to the item in the list, and reference to its sub-views (Text, Icon)
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View containerView;
            public final ImageView icon;
            public final TextView content;

             // What this list item refers to
            public SectionModule item;

            public ViewHolder(View view) {
                super(view);
                containerView = view;

                icon = (ImageView) view.findViewById(R.id.icon);
                content = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + content.getText() + "'";
            }
        }
    }
}
