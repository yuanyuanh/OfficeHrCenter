package com.example.officehrcenter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TabHost;

public class ProfileActivity extends Activity {

    private TabHost tabHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tabHost=(TabHost)findViewById(R.id.profile);
        tabHost.setup();

        TabHost.TabSpec spec;

        // --------------------------------Tab 1-----------------------------------
        spec = tabHost.newTabSpec("upcoming");	//create new tab specification
        spec.setContent(R.id.tab1);                 //add tab view content
        spec.setIndicator("Upcoming");              //put text on tab
        tabHost.addTab(spec);                       //put tab in TabHost container

        // --------------------------------Tab 2-----------------------------------
        spec = tabHost.newTabSpec("pending");	//create new tab specification
        spec.setContent(R.id.tab2);                 //add tab view content
        spec.setIndicator("Pending");               //put text on tab
        tabHost.addTab(spec);                       //put tab in TabHost container

        // --------------------------------Tab 3-----------------------------------
        spec = tabHost.newTabSpec("past");	    //create new tab specification
        spec.setContent(R.id.tab3);                 //add tab view content
        spec.setIndicator("Past");                  //put text on tab
        tabHost.addTab(spec);                       //put tab in TabHost container
    }
}
