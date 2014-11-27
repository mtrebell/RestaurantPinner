package com.restaurantapp.phoneapp.restaurantpinner;

/**
 * Created by fix on 25/11/2014.
 */
public class Notification {
    public final static int FRIEND = 0, PIN=1;
    public int type;
    public String restaurantUUID;
    public String restaurantName;
    public String address;

    public Notification(int type,String restaurantUUID,String restaurantName,String address){
        this.type=type;
        this.restaurantUUID=restaurantUUID;
        this.restaurantName=restaurantName;
        this.address=address;
    }


}
