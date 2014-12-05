package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
        TextView phone = (TextView) findViewById(R.id.restraunt_phone);
        TextView address = (TextView) findViewById(R.id.restraunt_address);
        TextView hours = (TextView) findViewById(R.id.restraunt_hours);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            default:
                return false;
        }
    }

    public void onClick(View view){
        int id = view.getId();
        if(id==R.id.back){
            Intent back= new Intent(this, PinActivity.class);
            back.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(back);
            finish();
        }
    }

}
