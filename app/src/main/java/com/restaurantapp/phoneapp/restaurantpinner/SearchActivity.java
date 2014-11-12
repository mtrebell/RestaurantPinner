package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.wallet.Address;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends Activity {
    UserGrid usergrid;
    ArrayList<MarkerOptions> markerList;
    double lat = -1, lng = -1;


    //Set up usergrid connection
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar);
        final TextView seekBarValue = (TextView) findViewById(R.id.seekbar_value);
        SeekListner seekListner = new SeekListner(seekBarValue);
        seekBar.setOnSeekBarChangeListener(seekListner);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        View view = getWindow().getDecorView();
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
        lp.gravity = Gravity.CENTER | Gravity.BOTTOM;
        getWindowManager().updateViewLayout(view, lp);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_clear:
                clear();

            case R.id.button_send:
                submit();
        }
    }

    private void clear() {

        //location
        EditText text = (EditText) findViewById(R.id.searchNam);
        text.setText("");

        //Name
        text = (EditText) findViewById(R.id.searchLoc);
        text.setText("");

        //set slider
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setProgress(20);
    }

    private void submit() {
        EditText text = (EditText) findViewById(R.id.searchLoc);
        final String location = text.getText().toString();

        text = (EditText) findViewById(R.id.searchNam);
        final String name = text.getText().toString();

        usergrid= ((MyApplication)getApplicationContext()).usergrid;

        if (usergrid != null) {
            //create thread here
            final EditText finalText = text;
            Thread thread = new Thread(new Runnable() {
                public void run() {

                    //GEOCODE LOCATION HERE
                    if (location != "" && lat == -1) {

                        Geocode geocoder = new Geocode();
                        double[] ltglng = geocoder.geocode(location);

                        lat = ltglng[0];
                        lng = ltglng[1];

                    }

                    SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar);

                    double dist = 0;
                    dist = seekBar.getProgress();

                    //if geocode isAlive wait turn geocode into function, put in here
                    ArrayList<MarkerOptions> markers = usergrid.restrauntSearch(name, lat, lng, dist, false);
                    Log.d("--------------------TESTING--------------", markers.toString());

                    //Done return to map
                    onComplete(markers);
                }
            });
            thread.start();
        }
            //Toast Searching.....
        //PUT ERROR MSG IF CANT CONNECT

        lat = -1;
        lng = -1;
    }
    public void updateLocation(double[] location){
        lat=location[0];
        lng =location[1];
    }

    public void onComplete( ArrayList<MarkerOptions> markers){
        Intent addIntent = new Intent(this,MainActivity.class);
        addIntent.putParcelableArrayListExtra("Markers",markers);
        Log.d("SENT MARKER LIST",markers.toString());
        startActivity(addIntent); //Is it possible to switch this to onResume????
        //TRY THIS LATER
        //this.getParent().addMarkers();
        //onData changed?
    }

}
