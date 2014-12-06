package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
    private Menu menu;
    Boolean search;
    Boolean displayDislike;
    Dialog dialog;
    ArrayList<MarkerOptions> markers;
    ArrayList<Pin> pins;
    ArrayList<Pin> filtered;
    ArrayList<Pin> fullPins;
    ArrayList<String> dislikeIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        search=true;
        displayDislike=false;
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
            fullPins = savedInstanceState.getParcelableArrayList("FullPins");
            displayDislike = savedInstanceState.getBoolean("DisplayDislike");
            dislikeIds = savedInstanceState.getStringArrayList("DislikeIds");
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
            if (extras.containsKey("FullPins")) {
                fullPins = extras.getParcelableArrayList("FullPins");
            }
            if (extras.containsKey("DislikeIds")){
                dislikeIds = extras.getStringArrayList("DislikeIds");
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
        this.menu = menu;
        UserGrid usergrid= ((MyApplication)getApplicationContext()).usergrid;
        setVisability(usergrid.getUID() != null); //if logged in
        return true;
    }

    public void setVisability(boolean visability){
        MenuItem item;
        //Login is opposite
        item = menu.findItem(R.id.action_login);
        item.setVisible(!visability);

        item = menu.findItem(R.id.action_account);
        item.setVisible(visability);
        item = menu.findItem(R.id.action_friends);
        item.setVisible(visability);
        item = menu.findItem(R.id.action_pins);
        item.setVisible(visability);
        item = menu.findItem(R.id.action_logout);
        item.setVisible(visability);
        item = menu.findItem(R.id.action_notification);
        item.setVisible(visability);
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
        outState.putParcelableArrayList("Pins", pins);
        outState.putParcelableArrayList("FullPins",fullPins);
        outState.putParcelableArrayList("Markers", markers);
        outState.putParcelableArrayList("Filtered", filtered);
        outState.putBoolean("Search", search);
        outState.putBoolean("DisplayDislike",displayDislike);
        outState.putStringArrayList("DislikeIds",dislikeIds);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null)
            return;
        Bundle extras = data.getExtras();
        System.out.println("requestCode: " + requestCode);
        switch(requestCode){
            case 1:
                search=true;
                markers=extras.getParcelableArrayList("Markers");
                if(dislikeIds != null)
                    if(!displayDislike)
                        filterDislike();
                displayPins();
                break;
            case 2:
                filtered = extras.getParcelableArrayList("Filtered");
                displayDislike = extras.getBoolean("DisplayDislike");
                displayPins();
                break;
            case 3:
                pins = extras.getParcelableArrayList("Pins");
                fullPins = extras.getParcelableArrayList("FullPins");
                dislikeIds = extras.getStringArrayList("DislikeIds");
                search = false;
                setVisability(true);
                displayPins();
                break;
            case 4:
                ArrayList<String> delete = extras.getStringArrayList("delete");
                if(delete!=null)
                    for(String d:delete) {
                        for (Pin pin : pins)
                            if (pin.uuid.equals(delete))
                                pins.remove(pin);
                        for (Pin pin2: fullPins)
                            if (pin2.uuid.equals(delete))
                                pins.remove(pin2);
                    }

                ArrayList<Pin> update = extras.getParcelableArrayList("update");
                if(update!=null) {
                    pins.addAll(update);
                    fullPins.addAll(update);
                }
                displayPins();
                break;
            case 5:
                Pin tempPin = extras.getParcelable("NewPin");
                System.out.println("New Pin");
                if(fullPins!=null && dislikeIds !=null && pins != null) {
                    fullPins.add(tempPin);

                    if (tempPin.types.contains("dislike")) {
                        dislikeIds.add(tempPin.uuid);
                    } else
                        pins.add(tempPin);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if(dialog!=null)
            dialog.dismiss();
        dialog=null;
        super.onDestroy();
    }

    private void displayPins(){
        Bundle data = new Bundle();
        if(filtered != null)
            data.putParcelableArrayList("Pins", filtered);
        else if(search)
            data.putParcelableArrayList("Markers", markers);
        else
            data.putParcelableArrayList("Pins", pins);

       adapter.setData(data);
       pager.getAdapter().notifyDataSetChanged();
    }

    private void openNewPin() {
        dialog= new AlertDialog.Builder(this)
                .setTitle("Add Pin")
                .setMessage("To add a pin tap and hold any location on the map")
                .setPositiveButton("Use Current", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                                Geocode geocoder = new Geocode(MainActivity.this);
                                double[] result = geocoder.getLocation();
                                openNewPin(result);

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

        filterIntent.putParcelableArrayListExtra("Pins", fullPins);
        filterIntent.putExtra("DisplayDislike", displayDislike);

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

    private  void openNewPin(double[] loc) {
        if (loc != null) {
            Intent newIntent = new Intent(this, NewPinActivity.class);
            newIntent.putExtra("lat", loc[0]);
            newIntent.putExtra("lng", loc[1]);
            startActivityForResult(newIntent, 5);
        }
    }

    private  void openPins(){
        Intent newIntent = new Intent(this,PinActivity.class);
        startActivityForResult(newIntent, 4);
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

    private void filterDislike(){
        int idx = 0;
        while(idx < markers.size()){
            int idIndex = 0;
            while(idIndex < dislikeIds.size()){
                if(markers.get(idx).getSnippet().equals(dislikeIds.get(idIndex))){
                    markers.remove(idx);
                    break;
                }else
                    idIndex++;
            }
            idx++;
        }
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

