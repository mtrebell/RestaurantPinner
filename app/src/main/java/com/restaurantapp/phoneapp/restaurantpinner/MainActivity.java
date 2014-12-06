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

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity {

    TabAdapter adapter;
    ViewPager pager;
    Boolean search;

    ArrayList<MarkerOptions> markers;
    ArrayList<Pin> pins;
    ArrayList<Pin> filtered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        search=true;
        adapter = new TabAdapter(getSupportFragmentManager());

        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);

        addTabs();
        if(savedInstanceState != null) {
            int index = savedInstanceState.getInt("index");
            getActionBar().setSelectedNavigationItem(index);
            getActionBar().setSelectedNavigationItem(index);
            pins = savedInstanceState.getParcelableArrayList("Pins");
            markers = savedInstanceState.getParcelableArrayList("Markers");
            filtered = savedInstanceState.getParcelableArrayList("Filtered");
            search = savedInstanceState.getBoolean("Search");
        }

        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            if (extras.containsKey("Markers")) {
                markers = extras.getParcelableArrayList("Markers");
                search = true;
            }
            if (extras.containsKey("Pins")) {
                pins = extras.getParcelableArrayList("Pins");
                search=false;
            }
            if (extras.containsKey("Filtered")){
                filtered = extras.getParcelableArrayList("Filtered");
            }
        }
        if(pins != null || markers != null || filtered != null)
            displayPins();

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
            item = menu.findItem(R.id.action_notification);
            item.setVisible(false);
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
            item = menu.findItem(R.id.action_notification);
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
            case R.id.action_changeview:
                changeView();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeView(){
        filtered=null;
        search = !search;
        displayPins();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int i = getActionBar().getSelectedNavigationIndex();
        outState.putInt("index", i);
        outState.putParcelableArrayList("Pins",pins);
        outState.putParcelableArrayList("Markers", markers);
        outState.putParcelableArrayList("Filtered", filtered);
        outState.putBoolean("Search", search);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        if(data == null)
            return;
        Bundle extras = data.getExtras();
        switch(requestCode){
            case 1:
                search=true;
                markers=extras.getParcelableArrayList("Markers");
                displayPins();
                break;
            case 2:
                filtered = extras.getParcelableArrayList("Filtered");
                displayPins();
                break;
            case 3:
                pins = extras.getParcelableArrayList("Pins");
                search = false;
                displayPins();
                break;
        }
    }

    private void displayPins(){
        Bundle data = new Bundle();
        if(filtered != null) {
            data.putParcelableArrayList("Pins", filtered);
        }else if(search) {
            data.putParcelableArrayList("Markers", markers);
        }else
            data.putParcelableArrayList("Pins", pins);

       adapter.setData(data);
       pager.getAdapter().notifyDataSetChanged();
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
        startActivityForResult(searchIntent, 1);
    }

    private void openFilter( ) {
        Intent filterIntent = new Intent(this,FilterActivity.class);

        if(search)
            filterIntent.putParcelableArrayListExtra("Markers", markers);

            filterIntent.putParcelableArrayListExtra("Pins", pins);

        startActivityForResult(filterIntent, 2);
    }

    private void openLogin() {
        Intent loginIntent = new Intent(this,LoginActivity.class);
        startActivityForResult(loginIntent, 3);
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




