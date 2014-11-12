package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.TabActivity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;


public class MarkerList extends ListFragment {

    String[] testData = new String[] {
            "India",
            "Pakistan",
            "Sri Lanka",
            "China",
            "Bangladesh",
            "Nepal",
            "Afghanistan",
            "North Korea",
            "South Korea",
            "Japan"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        MarkerAdapter adapter = new MarkerAdapter(inflater.getContext(), android.R.layout.simple_list_item_1);
        if(getArguments()!=null){
            ArrayList<MarkerOptions> markers = getArguments().getParcelableArrayList("Markers");
            adapter.addAll(markers);
        }
        setListAdapter(adapter);
       adapter.notifyDataSetChanged();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //get info
        //set tab to map
        //open dialog
        MarkerOptions marker  = (MarkerOptions) l.getAdapter().getItem(position);
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

    }

    public void showDialog(Bundle data){
        data.putInt("tab",1);
        getActivity().getActionBar().setSelectedNavigationItem(0);
        MarkerDialog dialog = new MarkerDialog(getActivity(),data);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getActivity().getActionBar().setSelectedNavigationItem(1);
            }
        });
        dialog.show();
    }



}
