package com.restaurantapp.phoneapp.restaurantpinner;


import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MyMapFragment extends Fragment {
    private LocationClient locationClient;
    private Location current;
    private GoogleMap map;
    private LatLngBounds bounds;



    private OnFragmentInteractionListener mListener;

    public static MyMapFragment newInstance(String param1, String param2) {
        MyMapFragment fragment = new MyMapFragment();
        return fragment;
    }

    public MyMapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_map, container, false);
        setMapTransparent((ViewGroup) view);
        if(map==null) {
            map = ((SupportMapFragment) getFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            setUpMap();
        }

        //--------------------Build Markers here?----------------------
        if(getArguments()!=null){
            ArrayList<MarkerOptions> markers = getArguments().getParcelableArrayList("Markers");
            if(markers!=null){
                for(MarkerOptions marker :markers)
                    map.addMarker(marker);
            }
        }

        return view;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    public void setUpMap(){
        map.setMyLocationEnabled(true);
        Location l = map.getMyLocation();
        LatLng location;
        if(l == null)
             location = new LatLng(53.533,-113.5);
        else
             location = new LatLng(l.getLatitude(),l.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
               .zoom(9)
              .target(location)
              .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) new MarkerClick());
    }

    public class MarkerClick implements GoogleMap.OnMarkerClickListener{
        @Override
        public boolean onMarkerClick(Marker marker) {
            final String uuid = marker.getSnippet();
            final UserGrid usergrid= ((MyApplication)getActivity().getApplicationContext()).usergrid;

            new AsyncTask<Void,Void,Bundle>() {
                @Override
                protected Bundle doInBackground(Void...voids) {

                    JSONObject restraunt = usergrid.restaurantInfo(uuid);
                    Log.d("Restraunt", restraunt.toString());
                    Bundle bundle = new Bundle();
                    bundle.putString("data",restraunt.toString());
                    return bundle;
                }

                protected void onPostExecute(Bundle result) {
                    showDialog(result);
                }
            }.execute();



            //Show in dialong
            //GRAB RESTRAUNT INFO
           // new thread(){
                //POPUP DIALOG WITH PINIT BUTTON
                    //IF CLICKED SMALL DIALOG WITH PINTYPE POPS UP
                        //IF RECCOMEND CLICKED SHOW A LIST OF FRIENDS
            //if successful return true
            //}
            return true;
        }
    }

    public void showDialog(Bundle data){
        data.putInt("tab",0);
        MarkerDialog dialog = new MarkerDialog(getActivity(),data);

        dialog.show();
    }

}
