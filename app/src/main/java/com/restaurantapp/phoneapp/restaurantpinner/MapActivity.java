package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MapActivity extends Activity {
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_info);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();

        if(bundle!=null)
            try {
                JSONObject data = new JSONObject(bundle.getString("data"));
                setUpMap(data);
                setUpInfo(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void setUpInfo(JSONObject restaurant) {
        Log.d("Finding items", "...........");
        TextView phone = (TextView) findViewById(R.id.restraunt_phone);
        TextView address = (TextView) findViewById(R.id.restraunt_address);
        TextView hours = (TextView) findViewById(R.id.restraunt_hours);
        Log.d("Found items", "...........");

        if (restaurant != null) {
            try {

                this.setTitle(restaurant.getString("name"));

                StringBuilder sb = new StringBuilder();
                JSONArray add = restaurant.getJSONArray("address");
                for (int i = 0; i < add.length(); i++) {
                    sb.append(add.getString(i));
                    sb.append(" ");
                }
                address.setText(sb.toString());

                sb = new StringBuilder();
                JSONArray hour = restaurant.getJSONArray("hours");
                for (int i = 0; i < hour.length(); i++) {
                    sb.append(hour.getString(i));
                    sb.append(" ");
                }
                hours.setText(sb.toString());

                phone.setText(restaurant.getString("phone"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void setUpMap(JSONObject data){
        try {
            Double lat = data.getJSONObject("location").getDouble("latitude");
            Double lng = data.getJSONObject("location").getDouble("longitude");
            LatLng loc = new LatLng(lat,lng);
            map.addMarker(new MarkerOptions().position(loc));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
            map.getUiSettings().setZoomControlsEnabled(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setUpData(JSONObject data){
        TextView phone = (TextView) findViewById(R.id.restraunt_phone);
        TextView address = (TextView) findViewById(R.id.restraunt_address);
        TextView hours = (TextView) findViewById(R.id.restraunt_hours);

        if (data != null) {
            try {

               JSONObject restaurant = new JSONObject(data.getString("data"));

                this.setTitle(restaurant.getString("name"));
                setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_action_newpin);

                StringBuilder sb = new StringBuilder();
                JSONArray add = restaurant.getJSONArray("address");
                for(int i=0;i<add.length();i++){
                    sb.append(add.getString(i));
                    sb.append(" ");
                }
                address.setText(sb.toString());

                sb = new StringBuilder();
                JSONArray hour = restaurant.getJSONArray("hours");
                for(int i=0;i<hour.length();i++){
                    sb.append(hour.getString(i));
                    sb.append(" ");
                }
                hours.setText(sb.toString());

                phone.setText(restaurant.getString("phone"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            default:
                return false;
        }
    }

}
