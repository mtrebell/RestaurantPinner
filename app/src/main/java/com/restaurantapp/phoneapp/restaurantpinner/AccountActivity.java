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


public class AccountActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account, menu);
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
        switch (view.getId()){
            case R.id.button_clear:
                clear();
                break;
            case R.id.buttonChangePass:
                changePassword();
                break;
            case R.id.buttonDeleteAcct:
                new DeleteUserDialog(this);
                break;
        }
    }

    private void clear() {

        EditText text = (EditText) findViewById(R.id.editOldPassword);
        text.setText("");

        text = (EditText) findViewById(R.id.editNewPassword);
        text.setText("");

        text = (EditText) findViewById(R.id.editReNewPassword);
        text.setText("");
    }

    private void changePassword(){
        EditText text = (EditText) findViewById(R.id.editOldPassword);
        final String oldPass = text.getText().toString();

        text = (EditText) findViewById(R.id.editNewPassword);
        final  String newPass = text.getText().toString();

        text = (EditText) findViewById(R.id.editReNewPassword);
        final String reNewPass = text.getText().toString();

        Toast toast;
        if(oldPass.isEmpty() || newPass.isEmpty() || reNewPass.isEmpty()){
            toast = Toast.makeText(this,"One or more fields is empty!!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        if(!newPass.equals(reNewPass)){
            toast = Toast.makeText(this,"New Password and Re-enter new password does not match", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        final UserGrid usergrid= ((MyApplication)getApplicationContext()).usergrid;

        new AsyncTask<Void,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(Void...voids){
                return usergrid.chgPassword("deleteThis",oldPass,newPass);
            }

            protected void onPostExecute(Boolean result){
                onComplete(result);
            }
        }.execute();
    }

    private void onComplete(Boolean result){
        Toast toast;

        if(!result){
            toast = Toast.makeText(this, "Password change unsuccessful", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }else{
            toast = Toast.makeText(this, "Password change Successful", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Intent openMainActivity= new Intent(this, MainActivity.class);
            openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(openMainActivity);
        }

    }

    private void deleteAccount(){
        final UserGrid usergrid = ((MyApplication)getApplicationContext()).usergrid;

        new AsyncTask<Void,Void,Boolean>(){
            protected Boolean doInBackground(Void...voids){
                return usergrid.deleteAccount(usergrid.getUID());
            }
            protected void onPostExecute(Boolean result){
                onDeleteUserComplete(result);}
        }.execute();
    }

    protected void onDeleteUserComplete(Boolean result){
        Toast toast;
        if(!result){
            toast = Toast.makeText(this,"Failed to delete account", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }else{
            toast = Toast.makeText(this,"Your account have been deleted", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            Intent openMainActivity= new Intent(this, MainActivity.class);
            openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(openMainActivity);
        }
    }

    public class DeleteUserDialog {
        public DeleteUserDialog(final Context context) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            LayoutInflater inflater = getLayoutInflater();
            final View convertView = (View) inflater.inflate(R.layout.dialog_delete_user, null);
            alertDialog.setView(convertView);
            alertDialog.setTitle("Delete Account");

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteAccount();
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
