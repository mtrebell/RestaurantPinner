package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendActivity extends Activity {

    UserGrid usergrid;
    Boolean editMode;
    FriendAdapter adapter;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        lv = (ListView) findViewById(R.id.list);
        usergrid= ((MyApplication)getApplicationContext()).usergrid;
        editMode=false;
        //Add friend button

        //Load Friends
        new AsyncTask<Void, Void, HashMap<String, String>>() {

            @Override
            protected HashMap<String, String> doInBackground(Void... voids) {
                HashMap<String,String> friends = usergrid.getFriends();
                return friends;
            }

            @Override
            protected void onPostExecute(HashMap<String, String> result) {
                //display pins
                if(result!=null)
                    displayFriends(result);
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
            switch (item.getItemId()){
                case R.id.action_add:
                    searchDialog();
                    return true;
                case R.id.action_home:
                    return true;
                default:
                    return false;
            }
        }

    public void searchDialog(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.user_search, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.username);


        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Search",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                findFriend(userInput.getText().toString());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

      AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.edit:
                changeButtons();
                break;
        }
    }

    public void changeButtons(){
        editMode=!editMode;
        adapter.setEdit(editMode);
        adapter.notifyDataSetChanged();
    }

    public void displayFriends(HashMap<String,String> result){
        Log.d("GOT HERE",result.toString());
        adapter = new FriendAdapter(result);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public class FriendAdapter extends BaseAdapter{

        HashMap<String,String> data;
        List<String> keys;
        Boolean editMode;

        public FriendAdapter(HashMap<String,String> data) {
            this.data=data;
            keys= new ArrayList<String>();
            for(String key : data.keySet())
                keys.add(key);
            editMode=true;
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

            TextView address = (TextView)convertView.findViewById(R.id.txtSub);
            TextView name = (TextView)convertView.findViewById(R.id.txtTitle);
            ImageView icon = (ImageView)convertView.findViewById(R.id.imgIcon);

            String friend =data.get(keys.get(pos));
            name.setText(friend);
            address.setText(keys.get(pos));

            //Add ICON

            if(editMode) {
                Button delete = (Button) convertView.findViewById(R.id.delete);
                delete.setOnClickListener(
                        new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Integer i = (Integer) view.getTag();
                                removeFriend(keys.get(i));
                                data.remove(keys.get(i));
                                keys.remove(i);
                                notifyDataSetChanged();
                            }
                        }
                );

            }

            //Friends icon
            //icon.setImageResource(r.getIdentifier("ic_"+ic, "drawable", getPackageName()));

            return convertView;
        }

        public void setEdit(boolean mode){
            editMode=mode;
        }

    }

    private void findFriend(String name) {
        new AsyncTask<String, Void, HashMap<String, String>>(){

            @Override
            protected HashMap<String, String> doInBackground(String... strings) {
                return usergrid.searchUser(strings[0]);
            }

            @Override
            protected void onPostExecute(HashMap<String, String> result) {
                if(result!=null && !result.isEmpty()){
                    friendDialog(result);
                }
                //Else error msg
                //Back to search dialog
            }
        }.execute(name);
    }


    public class FriendsDialog {
        HashMapAdapter adapter;
        ListView lv;
        public int selected;
        String username;
        List<String> friends;

        public FriendsDialog(HashMap<String,String> users,Context context) {
            friends = new ArrayList<String>();

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            LayoutInflater inflater = getLayoutInflater();
            View convertView = (View) inflater.inflate(R.layout.fragment_item_list, null);
            alertDialog.setView(convertView);
            alertDialog.setTitle("Choose Friend(s)");

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    addFriend(friends);
                }

            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            lv = (ListView) convertView.findViewById(R.id.list);
            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                    String friend = adapter.getItem(position);
                    if(friends.contains(friend))
                        friends.remove(friend);
                    else
                        friends.add(friend);
                }
            });

            adapter = new HashMapAdapter(users);
            lv.setAdapter(adapter);

            alertDialog.show();
        }

    }

    public void friendDialog(HashMap<String,String> result){
        new FriendsDialog(result,this);
    }

    private void addFriend(List<String> uuid) {
        new AsyncTask<List<String>,Void,Void>(){

            @Override
            protected Void doInBackground(List<String>... strings) {
                usergrid.requestFriend(strings[0]);
                return null;
            }
        }.execute(uuid);
    }



    private void removeFriend(String uuid) {
        new AsyncTask<String,Void,Void>(){

            @Override
            protected Void doInBackground(String... strings) {
                usergrid.removeFriend(strings[0]);
                return null;
            }
        }.execute(uuid);
    }
}
