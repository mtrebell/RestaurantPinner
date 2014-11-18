package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fix on 11/11/2014.
 */
public class MarkerDialog extends Dialog {
    Bundle info;
    JSONObject restaurant;
    Activity parent;

    public MarkerDialog(Context context,Bundle info,Activity parent) {
        super(context,android.R.style.Theme_Holo_Dialog);
        this.info=info;
        this.parent=parent;
    }

    public MarkerDialog(Context context, int theme) {
        super(context, theme);
    }

    protected MarkerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        super.onCreate(savedInstanceState);

        View view = getWindow().getDecorView();
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();

        setContentView(R.layout.dialog_marker);

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
                e.printStackTrace();
            }

            //for the button set listener
            //Open chose pin type dialog
            //Submit pin or
            //open chose friend dialog
            //have a aback button that goes back to map
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
                Bundle location = new Bundle();
                if(restaurant!=null) {
                    Intent addIntent = new Intent(parent,NewPinActivity.class);
                    try {
                        addIntent.putExtra("restaurant", restaurant.getString("uuid"));
                        parent.startActivity(addIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }



}

