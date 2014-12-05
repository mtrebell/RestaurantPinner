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

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class SearchActivity extends Activity {
    UserGrid usergrid;
    double lat = -1, lng = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar);
        final TextView seekBarValue = (TextView) findViewById(R.id.seekbar_value);
        SeekListener seekListener = new SeekListener(seekBarValue);
        seekBar.setOnSeekBarChangeListener(seekListener);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                break;

            case R.id.button_send:
                submit();
                break;

            case R.id.button_current:
                getLoc();
                break;
        }
    }

    private void clear() {

        EditText text = (EditText) findViewById(R.id.searchNam);
        text.setText("");

        text = (EditText) findViewById(R.id.searchLoc);
        text.setText("");

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setProgress(20);
    }

    private  void getLoc(){
        final Geocode geocoder = new Geocode(this);
        final double[] loc = geocoder.getLocation();
        lat= loc[0];
        lng=loc[1];

        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... voids) {
                return geocoder.reverseGeocode(loc[0],loc[1]);
            }

            @Override
            protected void onPostExecute(String address) {
                updateLocation(address);
            }
        }.execute();


    }
    public void updateLocation(String address){
        EditText text = (EditText) findViewById(R.id.searchLoc);
        text.setText(address);
    }

    private void submit() {
        EditText text = (EditText) findViewById(R.id.searchLoc);
        final String location = text.getText().toString();

        text = (EditText) findViewById(R.id.searchNam);
        final String name = text.getText().toString();

        usergrid= ((MyApplication)getApplicationContext()).usergrid;

            new AsyncTask<Object,Void, ArrayList<MarkerOptions>>(){

                @Override
                protected  ArrayList<MarkerOptions> doInBackground(Object... objects) {

                    if (!location.isEmpty() && lat == -1) {
                        Geocode geocoder = new Geocode(SearchActivity.this);
                        double[] ltglng = geocoder.geocode(location);

                        lat = ltglng[0];
                        lng = ltglng[1];
                    }

                    SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar);

                    double dist = 0;
                    dist = seekBar.getProgress();

                    return usergrid.restaurantSearch(name, lat, lng, dist, false);
                }

                @Override
                protected void onPostExecute( ArrayList<MarkerOptions> result) {
                    loadMarkers(result);

                }
            }.execute();

    }

    public void loadMarkers(ArrayList<MarkerOptions> markers){
        Intent addIntent = new Intent(this,MainActivity.class);
        addIntent.putExtra("Search",true);
        addIntent.putParcelableArrayListExtra("Markers",markers);
        startActivity(addIntent);
        lat = -1;
        lng = -1;
    }
}
