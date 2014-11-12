package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Application;

/**
 * Created by fix on 11/11/2014.
 */
public class MyApplication extends Application {
    public UserGrid usergrid = new UserGrid("http://192.168.2.156:8080/pinApp/resturantApp");
}
