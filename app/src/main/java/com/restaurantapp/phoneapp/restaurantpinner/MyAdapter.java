package com.restaurantapp.phoneapp.restaurantpinner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

class MyAdapter extends FragmentPagerAdapter {
    Bundle bundle;

    public MyAdapter(FragmentManager fm){
        super(fm);
        bundle=null;
    }

    @Override
    public Fragment getItem(int position) {
        System.out.println(position);
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new MyMapFragment();
                break;
            case 1:
                fragment = new MarkerList();
                break;
        }

        if(bundle!=null) {
            Log.d("-----------App-------------", "Adapter has the data");
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    @Override
    public int getCount(){
        return 2;
    }

    public void setData(Bundle data){
        bundle=data;
    }
}
