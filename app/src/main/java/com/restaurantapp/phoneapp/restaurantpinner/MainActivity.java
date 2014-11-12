package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.logging.Log;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity {

    MyAdapter adapter;
    ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // ArrayList<MarkerOptions> data = (ArrayList<MarkerOptions>) extras.get("markerList");

        adapter = new MyAdapter(getSupportFragmentManager());

        Bundle extras = getIntent().getExtras();

        if(extras!=null) {
            if (extras.containsKey("Markers")) {
                Toast.makeText(this, "Main got some data!", Toast.LENGTH_SHORT).show();
                Bundle test = new Bundle();
                test.putParcelableArrayList("Markers", extras.getParcelableArrayList("Markers"));
                adapter.setData(test);
            }
            if(extras.get("Markers")!=null){
                Toast.makeText(this,"Main got a marker array",Toast.LENGTH_SHORT).show();
            }
        }


        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(4);

        addTabs();

        if(savedInstanceState != null) {
            int index = savedInstanceState.getInt("index");
            getActionBar().setSelectedNavigationItem(index);
        }

        //Temp until figure out onClick bug
        Button button = (Button) findViewById(R.id.action_filter);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                openFilter();

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_actions, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                openSearch();
                return true;
            case R.id.action_newpin:
                openNewPin();
                return true;
            case R.id.action_notification:
                openNotification();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int i = getActionBar().getSelectedNavigationIndex();
        outState.putInt("index", i);
    }

        public void onClick(View view){
            switch ( view.getId()){
                case R.id.action_filter:
                    openFilter();
            }

        }

    private void openNewPin() {
        Intent addIntent = new Intent(this,NewPinActivity.class);
        startActivity(addIntent);
    }

    private void openSearch() {
        Intent searchIntent = new Intent(this,SearchActivity.class);
        startActivity(searchIntent);
    }

    private void openFilter( ) {
        Intent filterIntent = new Intent(this,FilterActivity.class);
        startActivity(filterIntent);
    }

    private void openNotification() {
        Intent notIntent = new Intent(this,Notification.class);
        startActivity(notIntent);
    }

    private void addTabs(){
        final ActionBar actionBar = getActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                pager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };

        // Add tabs, specifying the tab's text and TabListener
            actionBar.addTab(
                    actionBar.newTab()
                            .setText("Map")
                            .setTabListener(tabListener));

        actionBar.addTab(
                actionBar.newTab()
                        .setText("List")
                        .setTabListener(tabListener));
        }

    }




