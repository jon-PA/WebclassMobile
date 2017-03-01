/*
 * Created by Jonathan Acampora 2016
 * Copyright (c) 2016. All rights reserved
 *
 * Last modified 10/8/16 10:26 PM
 */

package com.acampora.webclassmobile;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acampora.webclassmobile.data.SectionModule;

import org.jsoup.Jsoup;

public class ModuleDetailFragment extends Fragment {

    private SectionModule module;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ModuleDetailFragment() {

    }

    public void setModule(SectionModule module) {
        this.module = module;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
         // If this fragment is a member of its own activity (on phones not tablets), give it a title
        if (appBarLayout != null) {
            if (module != null)
                appBarLayout.setTitle(module.name);
        }
    }

    public String superTrim (String input) {
        return input.replaceAll("\\s+", " ").replaceAll("\n", "").trim();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.module_detail, container, false);

        updateDisplay(rootView);

        return rootView;
    }

    public void updateDisplay (View rootView) {
        if (module != null) {
            String desc = module.description;
            if (desc == null)
                desc = "";
            ((TextView) rootView.findViewById(R.id.module_header)).setText(superTrim(module.name));
            ((TextView) rootView.findViewById(R.id.module_detail)).setText(Jsoup.parse(desc).text());
        }

    }
}
