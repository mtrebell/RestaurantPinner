//EDITMODE CONTROLS ARE REPETITIVE

package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class NotificationActivity extends Activity {

    UserGrid usergrid;
    NotAdapter adapter;
    ListView lv;
    Boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        lv = (ListView) findViewById(R.id.list);
        usergrid= ((MyApplication)getApplicationContext()).usergrid;
        editMode =false;

        getNotifications();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            changeButtons();
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeButtons() {
        editMode = !editMode;
        if (editMode)
            lv.setOnItemClickListener(null);

        //else
            //notification clickListener
        //if friend display details
        //if map display like in PINACTIVITY

        adapter.setEdit(editMode);
        adapter.notifyDataSetChanged();
    }

    public void getNotifications(){
        new AsyncTask<Void, Void, HashMap<String, Notification>>() {

            @Override
            protected HashMap<String, Notification> doInBackground(Void... voids) {
                return usergrid.getNotifications();
            }

            @Override
            protected void onPostExecute(HashMap<String, Notification> result) {
                if(result!=null)
                    displayNotifications(result);
            }
        }.execute();
    }

    public void displayNotifications(HashMap<String,Notification> result){
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

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            final String key = keys.get(pos);
            final Notification value = data.get(key);

            if(convertView==null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification, parent, false);

            TextView text = (TextView)convertView.findViewById(R.id.text);
            ImageView icon = (ImageView)convertView.findViewById(R.id.imgIcon);

            if(value.type==Notification.FRIEND){
                icon.setImageResource(R.drawable.ic_action_add_person);
                text.setText("New friend request from " + value.uuid);
            }
            else{
                icon.setImageResource(R.drawable.ic_action_newpin);
                text.setText("New Recommendation: " + value.name);
            }

            ImageButton delete = (ImageButton) convertView.findViewById(R.id.remove);
            ImageButton confirm = (ImageButton) convertView.findViewById(R.id.confirm);

            if(editMode) {
                delete.setVisibility(View.VISIBLE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        data.remove(key);
                        keys.remove(key);
                        notifyDataSetChanged();

                        if(value.type==Notification.PIN)
                            removeRecommend(value.uuid);
                        else
                            removeFriendReq(value.uuid);
                    }
                });

                confirm.setVisibility(View.VISIBLE);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        data.remove(key);
                        keys.remove(key);
                        notifyDataSetChanged();
                        if(value.type==Notification.PIN)
                            addRecommend(value.uuid);
                        else
                            addFriend(value.uuid);
                    }
                });
            }

            else{
               delete.setVisibility(View.INVISIBLE);
                confirm.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

        public  void setEdit(boolean mode){
            editMode = mode;
        }

        public boolean getEdit(){
            return editMode;
        }

    }

    private  void addFriend(String uuid){
        new AsyncTask<String,Void,Void>(){

            @Override
            protected Void doInBackground(String... strings) {
                usergrid.removeRequestFriend(strings[0]);
                usergrid.addFriend(strings[0]);
                return null;
            }
        }.execute(uuid);
    }

    private void removeFriendReq(String uuid) {
        new AsyncTask<String,Void,Void>(){

            @Override
            protected Void doInBackground(String... strings) {
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
                usergrid.removeRecommendation(strings[0]);
                return null;
            }
        }.execute(restaurant);

    }

    private void removeRecommend(String restaurant) {
        new AsyncTask<String,Void,Void>(){

            @Override
            protected Void doInBackground(String... strings) {
                usergrid.removeRecommendation(strings[0]);
                return null;
            }
        }.execute(restaurant);

    }

}
