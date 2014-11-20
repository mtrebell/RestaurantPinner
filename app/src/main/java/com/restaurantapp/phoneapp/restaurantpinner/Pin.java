package com.restaurantapp.phoneapp.restaurantpinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fix on 17/11/2014.
 */
public class Pin {
    public double lat,lng;
    public String uuid,name,address;
    public ArrayList<String> types;

    public Pin(String uuid,String name,String address,String type,double lat,double lng){
        this.uuid=uuid;
        this.name=name;
        this.address=address;
        this.lat=lat;
        this.lng=lng;
        types = new ArrayList<String>();
        types.add(type);
    }
}
