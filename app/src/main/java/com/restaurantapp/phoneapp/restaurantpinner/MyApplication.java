package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Application;

/**
 * Created by fix on 11/11/2014.
 */
public class MyApplication extends Application {

    //For test purposes only,should grab when login.

    public UserGrid usergrid = new UserGrid("https://api.usergrid.com/mtrebell/restaurantpinner","eb70891a-6f82-11e4-9e00-e722cf43f3ae","YWMtvYwM5HdsEeSh718PlTRPLwAAAUodW9NUTCqMnbWvygG8Xv9cX2NYn4h-EVc");
   // public UserGrid usergrid = new UserGrid("https://api.usergrid.com/mtrebell/restaurantpinner");
}
