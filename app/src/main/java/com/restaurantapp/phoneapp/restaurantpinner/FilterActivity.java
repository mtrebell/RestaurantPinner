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
    Boolean search;
    ArrayList<MarkerOptions> markers;
    ArrayList<Pin> pins;
    Boolean displayDislike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if (extras.containsKey("Markers")) {
                markers = extras.getParcelableArrayList("Markers");
                search = true;
            }
            if (extras.containsKey("Pins")) {
                pins = extras.getParcelableArrayList("Pins");
                search = false;
            }
            if(extras.containsKey("DisplayDislike")){
                displayDislike = extras.getBoolean("DisplayDislike");
            }
        }

        CheckBox dislikeBox;
        if(displayDislike){
            dislikeBox = (CheckBox)findViewById(R.id.chkDislike);
            dislikeBox.toggle();
        }

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

        filterOptions = (CheckBox)findViewById(R.id.chkDislike);
        if(filterOptions.isChecked()){
            selectedPinTypes.add("dislike");
            displayDislike = true;
        }else
            displayDislike = false;

        ArrayList<Pin>results;
        if(search)
            results = filterMarkers(selectedPinTypes);
        else
            results =  filterPins(selectedPinTypes);

        Intent addUserPinIntent = new Intent();
        addUserPinIntent.putParcelableArrayListExtra("Filtered",results);
        addUserPinIntent.putExtra("Search",false);
        addUserPinIntent.putExtra("DisplayDislike",displayDislike);
        setResult(2,addUserPinIntent);
        finish();
    }

    private ArrayList<Pin> filterPins(ArrayList<String>selectedPinTypes){
        ArrayList<Pin>filteredList = new ArrayList<Pin>();

        if(pins != null)
        for (Pin tempPin : pins){
            Boolean selected = false;

            for(String type : selectedPinTypes){
                if(tempPin.types.contains(type)){
                    selected = true;
                    break;
                }
            }
            if(selected){
                filteredList.add(tempPin);
            }
        }

        return filteredList;
    }

    private ArrayList<Pin> filterMarkers(ArrayList<String>selectedPinTypes){
        ArrayList<Pin>filteredList = filterPins(selectedPinTypes);
        ArrayList<Pin>filteredPins = new ArrayList<Pin>();

        for (Pin tempPin : filteredList){
            for(MarkerOptions marker : markers){
                if(tempPin.uuid.equals(marker.getSnippet())){
                    filteredPins.add(tempPin);
                }
            }
        }

        return filteredPins;
    }
}
