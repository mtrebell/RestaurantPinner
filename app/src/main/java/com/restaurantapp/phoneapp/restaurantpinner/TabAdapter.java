package com.restaurantapp.phoneapp.restaurantpinner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

class TabAdapter extends FragmentPagerAdapter {
    Bundle bundle;

    public TabAdapter(FragmentManager fm){
        super(fm);
        bundle=null;
    }

    @Override
    public Fragment getItem(int position) {
        System.out.println(position);
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new MyMapFragment();
                break;
            case 1:
                fragment = new MarkerList();
                break;
            default:
                return null;
        }

        if(bundle!=null) {
                fragment.setArguments(bundle);
        }

        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        UpdatableFragment f = (UpdatableFragment ) object;
        if (f != null) {
            f.update(bundle);
        }
        return super.getItemPosition(object);
    }

    @Override
    public int getCount(){
        return 2;
    }

    public void setData(Bundle data){
        bundle=data;
    }
}
