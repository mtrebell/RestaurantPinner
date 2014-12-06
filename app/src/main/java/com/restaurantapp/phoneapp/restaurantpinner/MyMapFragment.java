//SHOW DIALOG
package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.content.Intent;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MyMapFragment extends Fragment implements UpdatableFragment {
    private GoogleMap map;
    private OnFragmentInteractionListener listener;

    public static MyMapFragment newInstance(String param1, String param2) {
        return new MyMapFragment();
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

        if(getArguments()!=null){
            ArrayList<MarkerOptions> markers = getArguments().getParcelableArrayList("Markers");
            if(markers!=null){
                for(MarkerOptions marker :markers)
                    map.addMarker(marker);
            }
            else{
                ArrayList<Pin> pins = getArguments().getParcelableArrayList("Pins");
                for(Pin pin :pins)
                    map.addMarker(pin.marker);
            }
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            listener=null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    @Override
    public void update(Bundle data) {
        Log.d("ADDING", "update");
        if(map==null) {
            map = ((SupportMapFragment) getFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            setUpMap();
        }

        map.clear();
        if(data.containsKey("Markers")) {
            List<MarkerOptions> markers = data.getParcelableArrayList("Markers");
            if(markers!=null)
                for (MarkerOptions marker : markers) {
                    map.addMarker(marker);
                }
        }
        else if(data.containsKey("Pins")){
            List<Pin> pins = data.getParcelableArrayList("Pins");
            if(pins!=null)
                for(Pin pin: pins){
                    map.addMarker(pin.marker);
                }
        }
    }

    public interface OnFragmentInteractionListener {
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

    public void setUpMap() {
        map.setMyLocationEnabled(true);
        Location l = map.getMyLocation();
        LatLng location;
        if (l == null)
            location = new LatLng(53.533, -113.5);
        else
            location = new LatLng(l.getLatitude(), l.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .zoom(9)
                .target(location)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.setOnMarkerClickListener(new MarkerClick());
        map.setOnMapLongClickListener(new LongClick());
    }

    public class MarkerClick implements GoogleMap.OnMarkerClickListener{
        @Override
        public boolean onMarkerClick(Marker marker) {

            final String uuid = marker.getSnippet();
            final UserGrid usergrid= ((MyApplication)getActivity().getApplicationContext()).usergrid;

            new AsyncTask<Void,Void,Bundle>() {
                @Override
                protected Bundle doInBackground(Void...voids) {

                    JSONObject restaurant = usergrid.restaurantInfo(uuid);
                    Bundle bundle = new Bundle();
                    bundle.putString("data",restaurant.toString());
                    return bundle;
                }

                protected void onPostExecute(Bundle result) {
                    showDialog(result);
                }
            }.execute();

            return true;
        }
    }

    public class LongClick implements GoogleMap.OnMapLongClickListener{

        @Override
        public void onMapLongClick(LatLng latLng) {

            Intent addIntent = new Intent(getActivity(),NewPinActivity.class);
            addIntent.putExtra("lng",latLng.longitude);
            addIntent.putExtra("lat", latLng.latitude);
            startActivity(addIntent);
        }
    }

    public void showDialog(Bundle data){
        data.putInt("tab",0);
        MarkerDialog dialog = new MarkerDialog(getActivity(),data,getActivity());
        dialog.show();
    }

}
