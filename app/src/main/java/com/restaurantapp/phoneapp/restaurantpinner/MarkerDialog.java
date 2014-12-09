//THEME SHOULD BE SET IN STYLES CHECK THEME SETTINGS
package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MarkerDialog extends Dialog {
    Bundle info;
    JSONObject restaurant;
    Activity parent;

    public MarkerDialog(Context context,Bundle info,Activity parent) {
        super(context,android.R.style.Theme_Holo_Dialog);
        this.info=info;
        this.parent=parent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_marker);
        MyApplication app = (MyApplication) parent.getApplicationContext();
        boolean user = app.usergrid.getUID()!=null;
        Button pin = (Button)findViewById(R.id.restraunt_add);

        if(user)
            pin.setVisibility(View.VISIBLE);
        else
            pin.setVisibility(View.INVISIBLE);

        TextView phone = (TextView) findViewById(R.id.restraunt_phone);
        TextView address = (TextView) findViewById(R.id.restraunt_address);
        TextView hours = (TextView) findViewById(R.id.restraunt_hours);

        if (info != null) {
            try {

                restaurant = new JSONObject(info.getString("data"));

                this.setTitle(restaurant.getString("name"));
                setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_action_newpin);

                StringBuilder sb = new StringBuilder();
                JSONArray add = restaurant.getJSONArray("address");
                for(int i=0;i<add.length();i++){
                    sb.append(add.getString(i));
                    sb.append(" ");
                }
                address.setText(sb.toString());

                sb = new StringBuilder();
                JSONArray hour = restaurant.getJSONArray("hours");
                for(int i=0;i<hour.length();i++){
                    sb.append(hour.getString(i));
                    sb.append(" ");
                }
                hours.setText(sb.toString());

                phone.setText(restaurant.getString("phone"));

            } catch (JSONException e) {
            }
        }
        this.getWindow().setGravity(Gravity.BOTTOM);
        Button btn = (Button) findViewById(R.id.restraunt_close);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btn = (Button)findViewById(R.id.restraunt_add);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(restaurant!=null) {
                    Intent addIntent = new Intent(parent,NewPinActivity.class);
                    try {
                        addIntent.putExtra("restaurant", restaurant.getString("uuid"));
                        parent.startActivityForResult(addIntent,5);
                    } catch (JSONException e) {
                    }

                }
            }
        });
    }
}

