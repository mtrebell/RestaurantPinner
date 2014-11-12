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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pin);
        types = new ArrayList<String>();
        buildButtons();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null){
            Toast.makeText(this,"I got the data!!!",Toast.LENGTH_SHORT);
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
            //case R.id.button_current:
            case R.id.button_clear:
                cancel();
            case R.id.button_send:
                submit();
        }
    }

    public void cancel(){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }


    public void submit(){

        final UserGrid usergrid= ((MyApplication)getApplicationContext()).usergrid;

    //Build pin list
        new AsyncTask<Void,Void,ArrayList<MarkerOptions> >() {
            @Override
            protected ArrayList<MarkerOptions> doInBackground(Void...voids) {
                if(fav.isChecked())
                    types.add(UserGrid.FAV);
                if(fav.isChecked())
                    types.add(UserGrid.WISH);
                if(fav.isChecked())
                    types.add(UserGrid.LIKE);
                if(fav.isChecked())
                    types.add(UserGrid.DIS);

                if(types.isEmpty() && !rec.isChecked()){
                    Log.d("TYPES","NO TYPES SELECTED");
                    return null;
                }

                if(lat==-1) {
                        //ERROR MESSAGE
                        return null;
                }

                ArrayList<MarkerOptions> restaurant = usergrid.restrauntSearch("",lat,lng,0,false);

                return restaurant;
            }

            protected void onPostExecute(ArrayList<MarkerOptions> result) {
                onComplete(result);
            }
        }.execute();

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
    public void onComplete(final ArrayList<MarkerOptions> restraunts) {

        if(restraunts==null)
            return;

        if (restraunts.isEmpty()){
        Toast toast = Toast.makeText(this, "Could not find any restraunts", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        }
        else {
            ConfirmDialog dialog = new ConfirmDialog(restraunts, this);

            final UserGrid usergrid= ((MyApplication)getApplicationContext()).usergrid;

            new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {

                        //Add Recomendations
                        if(rec.isChecked()){
                            HashMap<String,String> friends =usergrid.getFriends();
                            if(friends!=null) {
                                getFriendsDialog(friends.keySet());
                            }

                            //choose friend(s)
                            //usergrid.addRecomendation();
                        }

                        //Add other pins
                        if(!types.isEmpty())
                            usergrid.addPin(uuid, types);

                        return null;

                }

                protected void onPostExecute(Void v){
                    endTask();
                }
            };
                }

            //Done start new activity

        }

    private void getFriendsDialog(Set<String> strings) {

    }

    private void endTask() {
        Intent openMainActivity= new Intent(this, MainActivity.class);
        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(openMainActivity);
    }
    //Start the confirm dialog
            //pass restraunts and get ref to map


    public class ConfirmDialog{
        MarkerAdapter adapter;
        ListView lv;
        public MarkerOptions item;

        public ConfirmDialog(ArrayList<MarkerOptions> restaurants,Context context) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewPinActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View convertView = (View) inflater.inflate(R.layout.fragment_item_list, null);
            alertDialog.setView(convertView);
            alertDialog.setTitle("Confirm Restaurant");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    uuid = item.getSnippet();
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    cancel();
                }
            });

            lv = (ListView) convertView.findViewById(R.id.list);
            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            adapter = new MarkerAdapter(context, android.R.layout.simple_list_item_1, restaurants);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                    lv.setItemChecked(position, true);
                    item = (MarkerOptions) adapter.getItem(position);
                    //highlight
                    adapter.notifyDataSetChanged();
                }
            });

            lv.setSelection(0);
            alertDialog.show();
        }
    }
}
