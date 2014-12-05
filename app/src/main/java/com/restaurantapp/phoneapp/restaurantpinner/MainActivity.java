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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


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
        pager.setOffscreenPageLimit(2);

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

        UserGrid usergrid= ((MyApplication)getApplicationContext()).usergrid;
        MenuItem item;
        if(usergrid.getUID() == null){
            item = menu.findItem(R.id.action_account);
            item.setVisible(false);
            item = menu.findItem(R.id.action_friends);
            item.setVisible(false);
            item = menu.findItem(R.id.action_pins);
            item.setVisible(false);
            item = menu.findItem(R.id.action_logout);
            item.setVisible(false);
            item = menu.findItem(R.id.action_login);
            item.setVisible(true);
        }else
        {
            item = menu.findItem(R.id.action_account);
            item.setVisible(true);
            item = menu.findItem(R.id.action_friends);
            item.setVisible(true);
            item = menu.findItem(R.id.action_pins);
            item.setVisible(true);
            item = menu.findItem(R.id.action_logout);
            item.setVisible(true);
            item = menu.findItem(R.id.action_login);
            item.setVisible(false);
        }
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
            case R.id.action_account:
                openAccount();
                return true;
            case R.id.action_logout:
                openLogout();
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

        public void onClick(View view){
            switch ( view.getId()){
                case R.id.action_filter:
                    openFilter();
            }

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
                                double[] loc = geocoder.getLocation();
                                return loc;
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

    private void openAccount(){
        Intent newIntent = new Intent(this,AccountActivity.class);
        startActivity(newIntent);
    }

    private void openLogout(){
        final UserGrid usergrid = ((MyApplication)getApplicationContext()).usergrid;

        new AsyncTask<Void,Void,Boolean>(){
            protected Boolean doInBackground(Void...voids ){
                return usergrid.logout(usergrid.getUID());
            }

            protected void onPostExecute(Boolean result){
                onComplete(result);
            }
        }.execute();
    }

    private void onComplete(Boolean result){
        Toast toast;
        if(result)
        {
            toast = Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT);
        }
        else{
            toast = Toast.makeText(this, "Logout unsuccessful", Toast.LENGTH_SHORT);
        }

        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();

        Intent openMainActivity= new Intent(this, MainActivity.class);
        // following flag used to prevent users to use back button to navigate to logged in menu
        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(openMainActivity);
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




