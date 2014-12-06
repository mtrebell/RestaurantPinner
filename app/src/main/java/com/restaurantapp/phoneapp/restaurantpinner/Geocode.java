package com.restaurantapp.phoneapp.restaurantpinner;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.location.LocationListener;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public final class Geocode implements LocationListener {
    private Context context;

    public Geocode(Context context){
        this.context=context;
    }

    public double[] geocode(String address){
        double[] latlng = new double[2];
        try {
            URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(address, "UTF-8") + "&sensor=false");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);

            InputStreamReader in = new InputStreamReader(con.getInputStream());

            StringBuilder response = new StringBuilder();
            String inputLine;

            BufferedReader br = new BufferedReader(in);
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

            JSONObject json = new JSONObject(response.toString());
            JSONObject location = json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            latlng[0] = location.getDouble("lat");
            latlng[1] = location.getDouble("lng");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return latlng;
    }

    public double[] getLocation(){

        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

       boolean gpsLoc = manager.isProviderEnabled( LocationManager.GPS_PROVIDER);
       boolean netLoc = manager.isProviderEnabled( LocationManager.NETWORK_PROVIDER);

        //Can not get location
        if(!gpsLoc && !netLoc)
            return null;

        Location loc = null;
        if(gpsLoc) {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(loc==null && netLoc){
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                loc=manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        else if (netLoc){
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            loc=manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        double[] latlng = null;

        if(loc!=null){
            latlng = new double[2];
            latlng[0]=loc.getLatitude();
            latlng[1]=loc.getLongitude();
        }

        manager.removeUpdates(this);
        return latlng;
    }

    public String reverseGeocode(double lat,double lng) {
        String address ="";
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+lng);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);

            InputStreamReader in = new InputStreamReader(con.getInputStream());

            StringBuilder response = new StringBuilder();
            String inputLine;

            BufferedReader br = new BufferedReader(in);
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

            JSONObject json = new JSONObject(response.toString());
            address = json.getJSONArray("results").getJSONObject(0).getString("formatted_address");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

