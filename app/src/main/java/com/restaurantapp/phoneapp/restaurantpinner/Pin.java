package com.restaurantapp.phoneapp.restaurantpinner;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class Pin {
    public double lat,lng;
    public String uuid,name,address;
    public ArrayList<String> types;
    public MarkerOptions marker;

    public Pin(String uuid,String name,String address,String type,double lat,double lng){
        this.uuid=uuid;
        this.name=name;
        this.address=address;
        this.lat=lat;
        this.lng=lng;
        types = new ArrayList<String>();
        types.add(type);

        LatLng latlng = new LatLng(lat,lng);
        marker = new MarkerOptions()
                .position(latlng)
                .title(name)
                .snippet(uuid);
    }
}
