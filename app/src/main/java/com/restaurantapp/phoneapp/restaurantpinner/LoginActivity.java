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
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class LoginActivity extends Activity {
    UserGrid usergrid;
    String uuid;
    String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.button_clear:
                clear();
                break;

            case R.id.button_login:
                login();
                break;
            case R.id.button_new_user:
                new NewUserDialog(this);
                break;
        }
    }

    private void clear() {

        //username
        EditText text = (EditText) findViewById(R.id.userId);
        text.setText("");

        //password
        text = (EditText) findViewById(R.id.pwd);
        text.setText("");
    }

    private void login(){
        EditText text = (EditText) findViewById(R.id.userId);
        final String username = text.getText().toString();

        text = (EditText) findViewById(R.id.pwd);
        final String password = text.getText().toString();

        if(username.isEmpty() || password.isEmpty()){
            // print error message
            Toast toast = Toast.makeText(this, "Username or password empty", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        final UserGrid usergrid= ((MyApplication)getApplicationContext()).usergrid;

        // attempt to login
        new AsyncTask<Void,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(Void...voids){
                return usergrid.login(username, password);
            }

            protected void onPostExecute(Boolean result){
                onComplete(result);
            }
        }.execute();
    }

    private void onComplete(Boolean result){
        Toast toast;
        if(!result){
            toast = Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }else{
            final UserGrid usergrid= ((MyApplication)getApplicationContext()).usergrid;
            uuid = usergrid.getUID();
            accessToken = usergrid.getAccessToken();
            toast = Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            new AsyncTask<Void, Void, HashMap<String, Pin>>(){
                @Override
                protected HashMap<String, Pin> doInBackground(Void... voids) {
                    HashMap<String, Pin> pins = usergrid.getPins();
                    return pins;
                }

                @Override
                protected void onPostExecute(HashMap<String, Pin> result){
                    onUserPinsComplete(result);
                }
            }.execute();

          /*  Intent openMainActivity= new Intent(this, MainActivity.class);
            openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            finish();
            startActivity(openMainActivity);*/
        }
        return;
    }

    private void onUserPinsComplete(HashMap<String, Pin> result){
        ArrayList<MarkerOptions> markerList = new ArrayList<MarkerOptions>();
        Iterator it = result.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry data = (Map.Entry)it.next();
            Pin tempPin = (Pin)data.getValue();
            markerList.add(tempPin.marker);
        }

        Intent addIntent = new Intent(this,MainActivity.class);
        addIntent.putParcelableArrayListExtra("Markers",markerList);
        Log.d("SENT MARKER LIST",markerList.toString());
        startActivity(addIntent);
    }

    private void newUser(String username, String pass, String email){
        final UserGrid usergrid= ((MyApplication)getApplicationContext()).usergrid;
        final String user = username;
        final String password = pass;
        final String e_mail = email;
        new AsyncTask<Void,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(Void...voids){
                return usergrid.addAccount(user, password, e_mail);
            }

            protected void onPostExecute(Boolean result){
                onNewUserComplete(result);
            }
        }.execute();
    }

    protected void onNewUserComplete(Boolean result){
        Toast toast;
        if(!result){
            toast = Toast.makeText(this, "New user creation failed", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }else{
            toast = Toast.makeText(this, "New user created!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Intent openMainActivity= new Intent(this, MainActivity.class);
            openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(openMainActivity);
        }
    }

    public class NewUserDialog{
        public NewUserDialog(final Context context){
            /*final Toast toast = Toast.makeText(context, "Creating new", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();*/

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            LayoutInflater inflater = getLayoutInflater();
            final View convertView = (View) inflater.inflate(R.layout.dialog_new_user, null);
            alertDialog.setView(convertView);
            alertDialog.setTitle("Create New User");

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String username;
                    String password;
                    String rePassword;
                    String email;
                    Toast toast;

                    EditText text = (EditText) convertView.findViewById(R.id.editUsername);
                    username = text.getText().toString();

                    text = (EditText) convertView.findViewById(R.id.editPass);
                    password = text.getText().toString();

                    text = (EditText) convertView.findViewById(R.id.editRePass);
                    rePassword = text.getText().toString();

                    text = (EditText) convertView.findViewById(R.id.editEmail);
                    email = text.getText().toString();

                    if(username.isEmpty() || password.isEmpty() || rePassword.isEmpty() || email.isEmpty()){
                        toast = Toast.makeText(context,"One or more fields above is empty", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }else if(!password.equals(rePassword)){
                        toast = Toast.makeText(context, "Password and re-enter password does not match", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                    newUser(username,password,email);
                    return;
                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface,int i) {
                }
            });
            alertDialog.show();
        }
    }
}
