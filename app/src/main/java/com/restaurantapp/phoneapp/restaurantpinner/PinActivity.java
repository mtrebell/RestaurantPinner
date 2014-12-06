//Editmode and change are repaticous

package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PinActivity extends Activity {

    UserGrid usergrid;
    HashMap<String, Pin> pins;
    Boolean editMode;
    PinAdapter adapter;
    ListView lv;
    List<String> add = new ArrayList<String>();
    List<String> remove = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        lv = (ListView) findViewById(R.id.list);
        lv.setItemsCanFocus(false);
        usergrid = ((MyApplication) getApplicationContext()).usergrid;
        editMode = false;

        new AsyncTask<Void, Void, HashMap<String, Pin>>() {

            @Override
            protected HashMap<String, Pin> doInBackground(Void... voids) {
                return usergrid.getPins();
            }

            @Override
            protected void onPostExecute(HashMap<String, Pin> result) {
                if (result != null)
                    displayPins(result);
            }
        }.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
        case R.id.action_edit:
            changeButtons();
              break;
            case R.id.action_home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeButtons() {
        editMode = !editMode;

        if (editMode)
            lv.setOnItemClickListener(null);

        else
            lv.setOnItemClickListener(new pinClickListener());

        adapter.setEdit(editMode);
        adapter.notifyDataSetChanged();
    }

    public void displayPins(HashMap<String, Pin> result) {
        pins = result;
        adapter = new PinAdapter(pins);
        lv.setAdapter(adapter);

        if (editMode)
            lv.setOnItemClickListener(null);
        else
            lv.setOnItemClickListener(new pinClickListener());

        adapter.notifyDataSetChanged();
    }

    public void update(final String restaurant, final List<String> add, final List<String> delete) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                usergrid.addPin(restaurant, add);
                usergrid.removePin(restaurant, delete);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.notifyDataSetChanged();
            }
        }.execute();

    }

    public class pinClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String restaurant = adapter.getId(i);
            new AsyncTask<String, Void, JSONObject>() {

                @Override
                protected JSONObject doInBackground(String... strings) {
                    return usergrid.restaurantInfo(strings[0]);
                }

                @Override
                protected void onPostExecute(JSONObject json) {
                    openMap(json.toString());
                }
            }.execute(restaurant);
        }
    }

    private  void openMap(String data){
        Intent intent = new Intent(PinActivity.this,MapActivity.class);
        intent.putExtra("data",data);
        startActivity(intent);
    }

    public class PinAdapter extends BaseAdapter {

        HashMap<String, Pin> data;
        List<String> keys;
        Boolean editMode;

        public PinAdapter(HashMap<String, Pin> data) {
            this.data = data;
            keys = new ArrayList<String>();
            for (String key : data.keySet())
                keys.add(key);
            editMode = false;
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
        public View getView(int pos, View convertView, final ViewGroup parent) {
            final String key = keys.get(pos);
            Pin restaurant = data.get(key);

            if (convertView == null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_friends, parent, false);

            TextView address = (TextView) convertView.findViewById(R.id.txtSub);
            TextView name = (TextView) convertView.findViewById(R.id.txtTitle);
            ImageView icon = (ImageView) convertView.findViewById(R.id.imgIcon);

            name.setText(restaurant.name);
            address.setText(restaurant.address);


            Button delete = (Button) convertView.findViewById(R.id.delete);
            Button edit = (Button) convertView.findViewById(R.id.edit);

            if (editMode) {

                delete.setOnClickListener(
                        new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                removePin(pins.get(key));
                                data.remove(key);
                                keys.remove(key);
                                notifyDataSetChanged();
                            }
                        }
                );
                delete.setVisibility(View.VISIBLE);

                edit.setOnClickListener(
                        new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                 new editDialog(parent.getContext(), pins.get(key));
                            }
                        }
                );
                edit.setVisibility(View.VISIBLE);
            } else {
                edit.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);
            }
            List<String> types = restaurant.types;
            if(types.contains(UserGrid.DIS))
                icon.setImageResource(R.drawable.ic_action_bad);
            else if(types.contains(UserGrid.WISH))
                icon.setImageResource(R.drawable.ic_action_important);
            else if(types.contains(UserGrid.REC))
                icon.setImageResource(R.drawable.ic_action_person);
            else if(types.contains(UserGrid.FAV))
                icon.setImageResource(R.drawable.ic_action_favorite);
            else
                icon.setImageResource(R.drawable.ic_action_good);

            return convertView;
        }

        public void setEdit(Boolean value) {
            editMode = value;
        }

        public boolean getEdit(){
            return editMode;
        }
    }

    private void removePin(Pin pin) {
        new AsyncTask<Pin, Void, Void>() {

            @Override
            protected Void doInBackground(Pin... pins) {
                Pin pin = pins[0];
                usergrid.removePin(pin.uuid, pin.types);
                return null;
            }
        }.execute(pin);
    }

    public class editDialog {
        Pin restaurant;

        public editDialog(Context context, final Pin pin) {

            restaurant = pin;

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.edit_dialog, null);
            alertDialog.setView(convertView);
            alertDialog.setTitle("Confirm Restaurant");
            alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    update(pin.uuid, add, remove);
                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //close dialog
                }
            });
            alertDialog.show();
            setUpToggle(convertView);
        }

        private void setUpToggle(View view) {
            ToggleButton fav = (ToggleButton) view.findViewById(R.id.fav);
            ToggleButton like = (ToggleButton) view.findViewById(R.id.like);
            ToggleButton wish = (ToggleButton) view.findViewById(R.id.wish);
            ToggleButton rec = (ToggleButton) view.findViewById(R.id.rec);
            ToggleButton dis = (ToggleButton) view.findViewById(R.id.dis);

            //Set toggle state
            fav.setChecked(restaurant.types.contains(UserGrid.FAV));
            like.setChecked(restaurant.types.contains(UserGrid.LIKE));
            wish.setChecked(restaurant.types.contains(UserGrid.WISH));
            rec.setChecked(restaurant.types.contains(UserGrid.REC));
            dis.setChecked(restaurant.types.contains(UserGrid.DIS));
        }

    }

    public void onToggleClick(View view) {
        boolean on = ((ToggleButton) view).isChecked();
        String type;
        switch (view.getId()) {
            case R.id.rec:
                type = UserGrid.REC;
                break;
            case R.id.fav:
                type = UserGrid.FAV;
                break;
            case R.id.wish:
                type = UserGrid.WISH;
                break;
            case R.id.like:
                type = UserGrid.LIKE;
                break;
            case R.id.dis:
                type = UserGrid.DIS;
                break;
            default:
                return;
        }

        if (on) {
            add.add(type);
            remove.remove(type);
        } else {
            add.remove(type);
            remove.add(type);
        }

    }

}
