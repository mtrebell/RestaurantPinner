//SHOW DIALOG IS REPATISOUS
package com.restaurantapp.phoneapp.restaurantpinner;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONObject;
import java.util.ArrayList;

public class MarkerList extends ListFragment {
    boolean search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ArrayAdapter adapter = new MarkerAdapter(inflater.getContext(), android.R.layout.simple_list_item_1);
        if(getArguments()!=null){
            ArrayList<MarkerOptions> markers = getArguments().getParcelableArrayList("Markers");
            if(markers!=null) {
                adapter.addAll(markers);
                search = true;
            }
            else {
                ArrayList<Pin> pin = getArguments().getParcelableArrayList("Pin");
                if(pin!=null) {
                    adapter.addAll(pin);
                    search=false;
                }
            }
        }
        setListAdapter(adapter);
       adapter.notifyDataSetChanged();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String uuid;

        if(search) {
            MarkerOptions marker = (MarkerOptions) l.getAdapter().getItem(position);
             uuid = marker.getSnippet();
        }

        else{
            Pin pin =(Pin) l.getAdapter().getItem(position);
            uuid = pin.uuid;
        }

        final UserGrid usergrid= ((MyApplication)getActivity().getApplicationContext()).usergrid;

        new AsyncTask<String,Void,Bundle>() {
            @Override
            protected Bundle doInBackground(String...strings) {

                JSONObject restraunt = usergrid.restaurantInfo(strings[0]);
                Log.d("Restraunt", restraunt.toString());
                Bundle bundle = new Bundle();
                bundle.putString("data",restraunt.toString());
                return bundle;
            }

            protected void onPostExecute(Bundle result) {
                showDialog(result);
            }
        }.execute(uuid);
    }

    public void showDialog(Bundle data){
        data.putInt("tab",1);
        getActivity().getActionBar().setSelectedNavigationItem(0);
        MarkerDialog dialog = new MarkerDialog(getActivity(),data,getActivity());
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getActivity().getActionBar().setSelectedNavigationItem(1);
            }
        });
        dialog.show();
    }

}
