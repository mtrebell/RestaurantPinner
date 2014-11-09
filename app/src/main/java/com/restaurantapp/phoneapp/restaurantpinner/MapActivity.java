package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapActivity.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapActivity#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MapActivity extends SupportMapFragment  {

    private LocationClient locationClient;
    private Location current;
    private GoogleMap map;

    public static MapActivity newInstance() {
        MapActivity fragment = new MapActivity();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_map, container, false);
        setMapTransparent((ViewGroup) view);
        return view;
    }


    private void setUpLocationClientIfNeeded() {
        if (locationClient == null) {
            //Toast.makeText();
           // locationClient = new LocationClient(); //HOW TO CREATE????
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void setMapTransparent(ViewGroup group) {
        int childCount = group.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = group.getChildAt(i);
            if (child instanceof ViewGroup) {
                setMapTransparent((ViewGroup) child);
            } else if (child instanceof SurfaceView) {
                child.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

}
