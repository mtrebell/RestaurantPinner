package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

    TabAdapter adapter;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new TabAdapter(getSupportFragmentManager());

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
        pager.setOffscreenPageLimit(2);

        addTabs();

        if(savedInstanceState != null) {
            int index = savedInstanceState.getInt("index");
            getActionBar().setSelectedNavigationItem(index);
        }

        Button button = (Button) findViewById(R.id.action_filter);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                openFilter();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            case R.id.action_login:
                openLogin();
                return true;
            case R.id.action_notification:
                openNotification();
                return true;
            case  R.id.action_pins:
                openPins();
                return true;
            case R.id.action_friends:
                openFriends();
                return true;
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

    private void openNewPin() {
        new AlertDialog.Builder(this)
                .setTitle("Add Pin")
                .setMessage("To add a pin tap and hold any location on the map")
                .setPositiveButton("Use Current", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new AsyncTask<Void, Void, double[]>() {

                            @Override
                            protected double[] doInBackground(Void... voids) {
                                Geocode geocoder = new Geocode(MainActivity.this);
                                return geocoder.getLocation();
                            }

                            @Override
                            protected void onPostExecute(double[] result) {
                                openNewPin(result);
                            }
                        }.execute();
                    }
                })
                .setIcon(android.R.drawable.ic_input_add)
                .show();
    }

    private void openSearch() {
        Intent searchIntent = new Intent(this,SearchActivity.class);
        startActivity(searchIntent);
    }

    private void openFilter( ) {
        Intent filterIntent = new Intent(this,FilterActivity.class);
        startActivity(filterIntent);
    }

    private void openLogin() {
        Intent loginIntent = new Intent(this,LoginActivity.class);
        startActivity(loginIntent);
    }

    private void openNotification() {
        Intent notIntent = new Intent(this,NotificationActivity.class);
        startActivity(notIntent);
    }

    private  void openNewPin(double[] loc){
        Intent newIntent = new Intent(this,NewPinActivity.class);
        newIntent.putExtra("lat",loc[0]);
        newIntent.putExtra("lng",loc[1]);
        startActivity(newIntent);
    }

    private  void openPins(){
        Intent newIntent = new Intent(this,PinActivity.class);
        startActivity(newIntent);
    }

    private void openFriends(){
        Intent newIntent = new Intent(this,FriendActivity.class);
        startActivity(newIntent);
    }

    private void addTabs(){
        final ActionBar actionBar = getActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                pager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };

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




