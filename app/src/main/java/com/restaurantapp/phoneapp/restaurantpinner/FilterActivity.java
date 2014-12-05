package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class FilterActivity extends Activity {
    UserGrid usergrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.buttonClear:
                clear();
                return;
            case R.id.buttonFilter:
                filter();
        }
    }

    private void clear(){
        CheckBox filterOptions = (CheckBox)findViewById(R.id.chkDislike);
        if(filterOptions.isChecked()){
            filterOptions.toggle();
        }

        filterOptions = (CheckBox)findViewById(R.id.chkFavourite);
        if(filterOptions.isChecked()){
            filterOptions.toggle();
        }

        filterOptions = (CheckBox)findViewById(R.id.chkLiked);
        if(filterOptions.isChecked()){
            filterOptions.toggle();
        }

        filterOptions = (CheckBox)findViewById(R.id.chkRecommended);
        if(filterOptions.isChecked()){
            filterOptions.toggle();
        }

        filterOptions = (CheckBox)findViewById(R.id.chkWishlist);
        if(filterOptions.isChecked()){
            filterOptions.toggle();
        }
    }

    private void filter(){
        final ArrayList<String>selectedPinTypes = new ArrayList<String>(); //keeps track of which filter is selected
        CheckBox filterOptions = (CheckBox)findViewById(R.id.chkFavourite);
        if(filterOptions.isChecked()){
            selectedPinTypes.add("fav");
        }

        filterOptions = (CheckBox)findViewById(R.id.chkWishlist);
        if (filterOptions.isChecked()){
            selectedPinTypes.add("wishlist");
        }

        filterOptions = (CheckBox)findViewById(R.id.chkLiked);
        if(filterOptions.isChecked()){
            selectedPinTypes.add("like");
        }

        filterOptions = (CheckBox)findViewById(R.id.chkRecommended);
        if(filterOptions.isChecked()){
            selectedPinTypes.add("reccomend");
        }

        new AsyncTask<Void, Void, HashMap<String, Pin> >(){
            @Override
            protected HashMap<String, Pin> doInBackground(Void... voids){
                UserGrid userGrid = ((MyApplication)getApplicationContext()).usergrid;
                return userGrid.getPins();
            }

            @Override
            protected void onPostExecute(HashMap<String, Pin> pins) {
                onPinRetrieveComplete(pins, selectedPinTypes);
            }
        }.execute();
    }

    private void onPinRetrieveComplete(HashMap<String, Pin>pins, ArrayList<String>selectedPinTypes){
        ArrayList<MarkerOptions>filteredList = new ArrayList<MarkerOptions>();
        Iterator it = pins.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry data = (Map.Entry)it.next();
            Pin tempPin = (Pin)data.getValue();

            Iterator pinIt = tempPin.types.iterator();
            Boolean selected = false;
            while(pinIt.hasNext()){
                if(selectedPinTypes.contains(pinIt.next().toString()) ){
                    selected = true;
                    break;
                }
            }
            if(selected){
                filteredList.add(tempPin.marker);
            }
        }

        Intent addUserPinIntent = new Intent(this,MainActivity.class);
        addUserPinIntent.putParcelableArrayListExtra("Markers",filteredList);
        startActivity(addUserPinIntent);
    }
}
