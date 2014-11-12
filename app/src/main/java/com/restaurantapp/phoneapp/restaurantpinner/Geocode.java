package com.restaurantapp.phoneapp.restaurantpinner;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by fix on 11/11/2014.
 */
public class Geocode {


    public double[] geocode(String address){
        double[] latlng = new double[2];
        try {
            URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(address, "UTF-8") + "&sensor=false");
            //URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=edmonton&sensor=false");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);


            InputStreamReader in = new InputStreamReader(con.getInputStream());

            StringBuffer response = new StringBuffer();
            String inputLine;

            BufferedReader br = new BufferedReader(in);
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

            JSONObject json = new JSONObject(response.toString());
            Log.d("LOCATION DATA",json.toString());
            Log.d("LOCATION DATA",json.getJSONArray("results").getJSONObject(0).toString());
            JSONObject location = json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            Log.d("LOCATION DATA",location.toString());
            latlng[0] = location.getDouble("lat");
            latlng[1] = location.getDouble("lng");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return latlng;
    }
}

