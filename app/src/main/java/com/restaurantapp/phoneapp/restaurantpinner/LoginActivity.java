package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class LoginActivity extends Activity {
    UserGrid usergrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usergrid= ((MyApplication)getApplicationContext()).usergrid;
        setContentView(R.layout.activity_login2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

        EditText text = (EditText) findViewById(R.id.userId);
        text.setText("");

        text = (EditText) findViewById(R.id.pwd);
        text.setText("");
    }

    private void login(){
        EditText text = (EditText) findViewById(R.id.userId);
        final String username = text.getText().toString();

        text = (EditText) findViewById(R.id.pwd);
        final String password = text.getText().toString();

        if(username.isEmpty() || password.isEmpty()){
            showMessage("Username or Password Empty");
            return;
        }

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
        if(!result){
            showMessage("Invalid Username or Password");
        }else{

            showMessage("Login Successful");
            back();
            final UserGrid usergrid= ((MyApplication)getApplicationContext()).usergrid;

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
        }
    }

    private void onUserPinsComplete(HashMap<String, Pin> result){
        ArrayList<MarkerOptions> markerList = new ArrayList<MarkerOptions>();
        Iterator it = result.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry data = (Map.Entry)it.next();
            Pin tempPin = (Pin)data.getValue();
            markerList.add(tempPin.marker);
        }

        Intent addUserPinIntent = new Intent(this,MainActivity.class);
        addUserPinIntent.putExtra("Search",false);
        addUserPinIntent.putParcelableArrayListExtra("Pins",markerList);
        startActivity(addUserPinIntent);
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
        if(!result){
            showMessage( "New user creation failed");
        }else{
            showMessage("New User Created");
            back();
        }
    }

    private void back(){
        Intent openMainActivity= new Intent(this, MainActivity.class);
        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(openMainActivity);
    }

    private void showMessage(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public class NewUserDialog{
        public NewUserDialog(final Context context){

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            LayoutInflater inflater = getLayoutInflater();
            final View convertView = inflater.inflate(R.layout.dialog_new_user, null);
            alertDialog.setView(convertView);
            alertDialog.setTitle("Create New User");

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String username;
                    String password;
                    String rePassword;
                    String email;

                    EditText text = (EditText) convertView.findViewById(R.id.editUsername);
                    username = text.getText().toString();

                    text = (EditText) convertView.findViewById(R.id.editPass);
                    password = text.getText().toString();

                    text = (EditText) convertView.findViewById(R.id.editRePass);
                    rePassword = text.getText().toString();

                    text = (EditText) convertView.findViewById(R.id.editEmail);
                    email = text.getText().toString();

                    if(username.isEmpty() || password.isEmpty() || rePassword.isEmpty() || email.isEmpty()){
                        showMessage("One or more of the above fields is empty");
                        return;
                    }else if(!password.equals(rePassword)){
                        showMessage("Passwords don't match");
                        return;
                    }
                    newUser(username,password,email);
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
