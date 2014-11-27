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


public class NotificationActivity extends Activity {

    UserGrid usergrid;
    HashMap<String,Pin> pins;
    NotAdapter adapter;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        lv = (ListView) findViewById(R.id.list);
        usergrid= ((MyApplication)getApplicationContext()).usergrid;

        new AsyncTask<Void, Void, HashMap<String, Notification>>() {

            @Override
            protected HashMap<String, Notification> doInBackground(Void... voids) {
                HashMap<String,Notification> friends = usergrid.getNotifications();
                return friends;
            }

            @Override
            protected void onPostExecute(HashMap<String, Notification> result) {
                if(result!=null)
                    displayNotifications(result);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view){
    }

    public void displayNotifications(HashMap<String,Notification> result){
        Log.d("GOT HERE",result.toString());
        adapter = new NotAdapter(result);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public class NotAdapter extends BaseAdapter{

        HashMap<String,Notification> data;
        List<String> keys;
        Boolean editMode;

        public NotAdapter(HashMap<String,Notification> data) {
            this.data=data;
            keys= new ArrayList<String>();
            for(String key : data.keySet())
                keys.add(key);
            editMode=false;
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
            final Notification value = (Notification)getItem(pos);

            if(convertView==null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_friends, parent, false);

            TextView text = (TextView)convertView.findViewById(R.id.text);
            ImageView icon = (ImageView)convertView.findViewById(R.id.imgIcon);
            ImageButton delete = (ImageButton) convertView.findViewById(R.id.remove);
            ImageButton confirm = (ImageButton) convertView.findViewById(R.id.confirm);

            if(value.type==Notification.FRIEND){
                icon.setImageResource(R.drawable.ic_action_add_person);
                text.setText("New friend request from " + value.restaurantName);
            }
            else{
                icon.setImageResource(R.drawable.ic_action_newpin);
                text.setText("New Reccomendation: " + value.restaurantName);
            }

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(value.type==Notification.PIN)
                        removeRecommend(value.restaurantUUID);
                    else
                        removeFriendReq(value.restaurantUUID);
                }
            });

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(value.type==Notification.PIN)
                        addRecommend(value.restaurantUUID);
                    else
                        addFriend(value.restaurantUUID);
                }
            });


            return convertView;
        }

    }

    private  void addFriend(String uuid){
        new AsyncTask<String,Void,Void>(){

            @Override
            protected Void doInBackground(String... strings) {
                String uuid = strings[0];
                usergrid.addFriend(strings[0]);
                return null;
            }
        }.execute(uuid);
    }

    private void removeFriendReq(String uuid) {
        new AsyncTask<String,Void,Void>(){

            @Override
            protected Void doInBackground(String... strings) {
                String uuid = strings[0];
                usergrid.removeRequestFriend(strings[0]);
                return null;
            }
        }.execute(uuid);
    }

    private void addRecommend(String restaurant) {
        new AsyncTask<String,Void,Void>(){

            @Override
            protected Void doInBackground(String... strings) {
                List<String> pin = new ArrayList<String>();
                pin.add(UserGrid.REC);
                usergrid.addPin(strings[0],pin);
                return null;
            }
        }.execute(restaurant);
    }

    private void removeRecommend(String restaurant) {
        new AsyncTask<String,Void,Void>(){

            @Override
            protected Void doInBackground(String... strings) {
                String uuid = strings[0];
                usergrid.removeRecomendation(strings[0]);
                return null;
            }
        }.execute(restaurant);
    }

}
