package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class NewPinActivity extends Activity {

    double lat=-1;
    double lng=-1;

    CheckBox fav;
    CheckBox like;
    CheckBox wish;
    CheckBox rec;
    CheckBox dis;

    List<String> types;
    private String uuid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pin); //Change this view
        types = new ArrayList<String>();
        buildButtons();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null){
         uuid = extras.getString("restaurant");
         lat=extras.getDouble("lat");
         lng=extras.getDouble("lng");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        View view = getWindow().getDecorView();
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
        lp.gravity = Gravity.CENTER | Gravity.BOTTOM;
        getWindowManager().updateViewLayout(view, lp);
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.button_clear:
                endTask();
            case R.id.button_send:
                submit();
        }
    }


    public void submit() {

        final UserGrid usergrid = ((MyApplication) getApplicationContext()).usergrid;

        if (fav.isChecked())
            types.add(UserGrid.FAV);
        if (wish.isChecked())
            types.add(UserGrid.WISH);
        if (like.isChecked())
            types.add(UserGrid.LIKE);
        if (dis.isChecked())
            types.add(UserGrid.DIS);

        if (types.isEmpty() && !rec.isChecked()) {
            endTask();
        }

        else {

            if (uuid != null) {
                if (rec.isChecked())
                    getFriends();
                else
                    addPin(null);

            }

            //Build pin list
            else {
                new AsyncTask<Void, Void, ArrayList<MarkerOptions>>() {
                    @Override
                    protected ArrayList<MarkerOptions> doInBackground(Void... voids) {
                        if (lat == -1) {
                            return null;
                        }

                        return usergrid.restaurantSearch("", lat, lng, 0, false);

                    }

                    protected void onPostExecute(ArrayList<MarkerOptions> result) {
                        launchConfirm(result);
                    }
                }.execute();

            }
        }
    }

    public void launchConfirm(ArrayList<MarkerOptions> restaurants){
        if (restaurants==null || restaurants.isEmpty()){
            Toast toast = Toast.makeText(this, "Could not find any restraunts", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else
            new ConfirmDialog(restaurants,this);
    }

    public void buildButtons(){
        fav = (CheckBox)findViewById(R.id.chkFavourite);
        like = (CheckBox)findViewById(R.id.chkLiked);
        wish = (CheckBox)findViewById(R.id.chkWishlist);
        rec = (CheckBox)findViewById(R.id.chkRecommended);
        dis= (CheckBox)findViewById(R.id.chkDislike);
    }

    public void getFriends() {

    if(rec.isChecked()){
        final UserGrid usergrid = ((MyApplication) getApplicationContext()).usergrid;

        new AsyncTask<Void,Void,HashMap<String,String>>(){

            @Override
            protected HashMap<String, String> doInBackground(Void... voids) {
                return usergrid.getFriends();
            }

            protected void onPostExecute(HashMap<String,String> results){
                if(results!=null){
                    new FriendDialog(results, NewPinActivity.this) {
                        @Override
                        protected void setButtons() {
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    addPin(friends);
                                }
                            });
                            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    endTask();
                                }
                            });
                        }
                    };
                }
                else {
                     addPin(null);
                }
            }
        }.execute();
       }
}

    public void addPin(List<String> friends){
        final UserGrid usergrid = ((MyApplication) getApplicationContext()).usergrid;
       new AsyncTask<List<String>,Void,Void>() {

            @Override
            protected Void doInBackground(List<String>... friends) {
                if(friends[0]!=null && !friends[0].isEmpty())
                    usergrid.addRecommendation(uuid,friends[0]);
                if(!types.isEmpty())
                    usergrid.addPin(uuid,types);
                return null;
            }

            protected void onPostExecute(Void voids) {
                populatePin();
            }
        }.execute(friends);
    }

    private void endTask() {
        uuid =null;
        lat=-1;
        lng=-1;

        finish();
    }

    private void populatePin(){
        Log.d("GOT HERE","POPULATE PIN");
        final UserGrid usergrid =  ((MyApplication) getApplicationContext()).usergrid;
        new AsyncTask<Void,Void,JSONObject>(){

            @Override
            protected JSONObject doInBackground(Void... voids) {
                return usergrid.restaurantInfo(uuid);
            }
            @Override
            protected void onPostExecute(JSONObject json) {
                fillInfo(json);
            }
        }.execute();
    }

    private void fillInfo(JSONObject restaurantInfo){
        Log.d("GOT HERE",restaurantInfo.toString());
        Pin newPin;
        try {
            System.out.println("Fill Info");
            String name = restaurantInfo.getString("name");
            JSONObject latlng = restaurantInfo.getJSONObject("location");
            double lat = latlng.getDouble("latitude");
            double lng = latlng.getDouble("longitude");
            newPin = new Pin(uuid,name,types,lat,lng);
            Log.d("GOT HERE",newPin.uuid);
            Intent newPinIntent = new Intent();
            newPinIntent.putExtra("NewPin",newPin);
            this.setResult(5,newPinIntent);
            finish();
        }catch (JSONException e){
            finish();
        }
    }

    public class ConfirmDialog{
        MarkerAdapter adapter;
        ListView lv;
        public int selected =0;

        public ConfirmDialog(ArrayList<MarkerOptions> restaurants,Context context) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.fragment_item_list, null);
            alertDialog.setView(convertView);
            alertDialog.setTitle("Confirm Restaurant");

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    uuid = adapter.getItem(selected).getSnippet();
                    Log.d("GOT UUID",uuid);
                    setuuid(uuid);

                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    endTask();
                }
            });

            lv = (ListView) convertView.findViewById(R.id.list);
            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            lv.setSelector(R.drawable.selected);
            adapter = new MarkerAdapter(context, android.R.layout.simple_list_item_1, restaurants);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                    selected = position;
                }
            });

            alertDialog.show();
        }
    }

    public void setuuid(String uuid){
        this.uuid=uuid;
        if (rec.isChecked())
            getFriends();
        else
            addPin(null);
    }

}
