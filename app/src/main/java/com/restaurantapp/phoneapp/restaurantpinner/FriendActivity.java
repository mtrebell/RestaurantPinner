//Maybe view with an editMode
//EDITMODE IS REDUNDENT

package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FriendActivity extends Activity {

    UserGrid usergrid;
    Boolean editMode;
    FriendAdapter adapter;
    ListView lv;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        lv = (ListView) findViewById(R.id.list);
        usergrid= ((MyApplication)getApplicationContext()).usergrid;
        editMode=false;
        dialog =null;
        //Add friend button

        //Load Friends
        new AsyncTask<Void, Void, HashMap<String, String>>() {

            @Override
            protected HashMap<String, String> doInBackground(Void... voids) {
                return usergrid.getFriends();
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
        getMenuInflater().inflate(R.menu.menu_friend, menu);
        return true;
    }

    @Override

        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
                case R.id.action_add:
                    searchDialog();
                    return true;
                case R.id.action_home:
                    finish();
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
                            public void onClick(DialogInterface d,int id) {
                                dialog =null;
                                d.cancel();
                            }
                        });

      AlertDialog alertDialog = alertDialogBuilder.create();
        dialog = alertDialog;
        alertDialog.show();
    }

    public void displayFriends(HashMap<String,String> result){
        adapter = new FriendAdapter(result);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void notFoundMsg(){
        Toast.makeText(this,"Could Not Find User",Toast.LENGTH_SHORT).show();
        searchDialog();
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

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            final String key = keys.get(pos);

            if(convertView==null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_friends, parent, false);

            TextView address = (TextView)convertView.findViewById(R.id.txtSub);
            TextView name = (TextView)convertView.findViewById(R.id.txtTitle);
            Button delete = (Button)convertView.findViewById(R.id.delete);
            Button edit = (Button)convertView.findViewById(R.id.edit);

            String email =data.get(keys.get(pos));
            name.setText(keys.get(pos));
            address.setText(email);
            edit.setVisibility(View.GONE);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeFriend(key);
                    data.remove(key);
                    keys.remove(key);
                    notifyDataSetChanged();
                }
            });

            return convertView;
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
                    new FriendDialog(result,FriendActivity.this){

                        @Override
                        protected void setButtons() {
                            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog=null;
                                    dialogInterface.cancel();
                                }
                            });
                            alertDialog.setPositiveButton("Add",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d("Friends",friends.toString());
                                    addFriend(friends);
                                }
                            });
                        }
                    };
                }
                //Else error msg
                else
                    notFoundMsg();
                //Back to search dialog
            }
        }.execute(name);
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

    @Override
    protected void onStop() {
        if (dialog != null){
            dialog.dismiss();
            dialog=null;
    }
        super.onPause();
    }
}
