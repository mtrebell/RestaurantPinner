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
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class NewPinActivity extends Activity {

    double lat=-1;
    double lng=-1;

    ToggleButton fav;
    ToggleButton like;
    ToggleButton wish;
    ToggleButton rec;
    ToggleButton dis;

    List<String> types;
    private String uuid;
    UserGrid usergrid;

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

        usergrid= ((MyApplication)getApplicationContext()).usergrid;
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
            //case R.id.button_current:
            case R.id.button_clear:
                endTask();
            case R.id.button_send:
                submit();
        }
    }


    public void submit() {
        Log.d("Trying to sumbit","------------");

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
            Log.d("TYPES", "NO TYPES SELECTED");
            endTask();
        }

        else {

            if (uuid != null) {
                Log.d("GOT UUID", uuid);
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
                        Log.d("Finding Restraunts","------------");
                        if (lat == -1) {
                            //ERROR MESSAGE
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

    public void launchConfirm(ArrayList<MarkerOptions> restraunts){
        if (restraunts==null || restraunts.isEmpty()){
            Toast toast = Toast.makeText(this, "Could not find any restraunts", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else
            new ConfirmDialog(restraunts,this);
    }

    public void buildButtons(){
        fav = (ToggleButton)findViewById(R.id.fav);
        like = (ToggleButton)findViewById(R.id.like);
        wish = (ToggleButton)findViewById(R.id.wish);
        rec = (ToggleButton)findViewById(R.id.rec);
        dis= (ToggleButton)findViewById(R.id.dis);
    }

    //load confirmdialog
    //addPin
    public void getFriends() {

        //Add Recomendations
    if(rec.isChecked()){
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
        new AsyncTask<String,Void,Void>() {

            @Override
            protected Void doInBackground(String... friends) {
                if(friends!=null && friends.length!=0)
                    usergrid.addRecommendation(uuid,friends);
                if(!types.isEmpty())
                    usergrid.addPin(uuid,types);
                return null;
            }

            protected void onPostExecute(Void voids) {
                endTask();
            }

        }.execute((String[])friends.toArray());
    }

    private void endTask() {
        uuid =null;
        lat=-1;
        lng=-1;
        Intent openMainActivity= new Intent(this, MainActivity.class);
        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(openMainActivity);
    }
    //Start the confirm dialog
            //pass restraunts and get ref to map


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
