package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;


public class LoginActivity extends Activity {
    UserGrid usergrid;
    String uuid;
    String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        Intent intent = getIntent();
        if(savedInstanceState != null) {
            int index = savedInstanceState.getInt("index");
            getActionBar().setSelectedNavigationItem(index);
        }
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
        if(!result){
            Toast toast = Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }else{
            final UserGrid usergrid= ((MyApplication)getApplicationContext()).usergrid;
            uuid = usergrid.getUID();
            accessToken = usergrid.getAccessToken();

            Intent openMainActivity= new Intent(this, MainActivity.class);
            openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(openMainActivity);
        }
        return;
    }
}
