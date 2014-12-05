package com.restaurantapp.phoneapp.restaurantpinner;

public class Notification {
    public final static int FRIEND = 0, PIN=1;
    public int type;
    public String uuid;
    public String name;
    public String address;


    public Notification(int type,String uuid,String name,String address){
        this.type=type;
        this.uuid = uuid;
        this.name = name;
        this.address=address;
    }

}
