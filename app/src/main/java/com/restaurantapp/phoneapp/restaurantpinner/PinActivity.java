package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PinActivity extends Activity {

    UserGrid usergrid;
    HashMap<String,Pin> pins;
    Boolean edit;
    PinAdapter adapter;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        lv = (ListView) findViewById(R.id.list);
        usergrid= ((MyApplication)getApplicationContext()).usergrid;
        edit=false;


        //Load Pins
        new AsyncTask<Void, Void, HashMap<String, Pin>>() {

            @Override
            protected HashMap<String, Pin> doInBackground(Void... voids) {
                HashMap<String,Pin> pins = usergrid.getPins();
                return pins;
            }

            @Override
            protected void onPostExecute(HashMap<String, Pin> result) {
                //display pins
                if(result!=null)
                    displayPins(result);
            }
        }.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    //Open Map and show pin info
    //Include Activty in bundle so return to it when done
    public void showOnMap(String uuid){
        //get restraunt data
        //get pin icons
    }

    public void displayPins(HashMap<String,Pin> result){
        Log.d("GOT HERE",result.toString());
        pins=result;
        adapter = new PinAdapter(pins);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public class PinAdapter extends BaseAdapter{

        HashMap<String,Pin> data;
        List<String> keys;
        Boolean edit;

        public PinAdapter(HashMap<String,Pin> data) {
            this.data=data;
            keys= new ArrayList<String>();
            for(String key : data.keySet())
                keys.add(key);
            edit=false;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(keys.get(i));
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public String getId(int i) {
            return keys.get(i);
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            String key = keys.get(pos);
            String Value = getItem(pos).toString();

            if(convertView==null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_friends, parent, false);

            ImageButton delete = (ImageButton)convertView.findViewById(R.id.delete);
            TextView address = (TextView)convertView.findViewById(R.id.txtSub);
            TextView name = (TextView)convertView.findViewById(R.id.txtTitle);
            ImageView icon = (ImageView)convertView.findViewById(R.id.imgIcon);

            //May add address
           // address.setText(users.get(keys[pos]));
            Pin restraunt =data.get(keys.get(pos));
            name.setText(restraunt.name);
            address.setText(restraunt.address);

            //Add address
            //Add ICON

            if(edit) {
                delete.setOnClickListener(
                        new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Integer i = (Integer) view.getTag();
                                removePin(keys.get(i));
                                data.remove(keys.get(i));
                                keys.remove(i);
                                notifyDataSetChanged();
                            }
                        }
                );
                delete.setVisibility(View.VISIBLE);
            }


            //Set image according to first pin
            String ic = data.get(keys.get(pos)).types.get(0);
            Resources r =getResources();
            r.getIdentifier("ic_"+ic, "drawable", getPackageName());
            icon.setImageResource(r.getIdentifier("ic_"+ic, "drawable", getPackageName()));

            return convertView;
        }

        public void setEdit(Boolean value){
            edit=value;
        }
    }

    private void removePin(String uuid) {
        new AsyncTask<String,Void,Void>(){

            @Override
            protected Void doInBackground(String... strings) {
                String uuid = strings[0];
                List<String> delete = pins.get(uuid).types;
                usergrid.removePin(uuid,delete);
                return null;
            }
        }.execute(uuid);
    }

}
