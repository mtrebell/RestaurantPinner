package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Dialog;
import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fix on 11/11/2014.
 */
public class MarkerDialog extends Dialog {
    Bundle info;
    public MarkerDialog(Context context,Bundle info) {
        super(context);
        this.info=info;
    }

    public MarkerDialog(Context context, int theme) {
        super(context, theme);
    }

    protected MarkerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        View view = getWindow().getDecorView();
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();

        setContentView(R.layout.dialog_marker);
        TextView name = (TextView) findViewById(R.id.restraunt_name);
        TextView address = (TextView) findViewById(R.id.restraunt_address);
        TextView hours = (TextView) findViewById(R.id.restraunt_hours);
        ImageView pin = (ImageView) findViewById(R.id.pin);
        //Set all the fields
        if (info != null) {
            try {
                JSONObject restaurant = new JSONObject(info.getString("data"));
                name.setText(restaurant.getString("name"));
                address.setText(restaurant.getJSONArray("address").toString());
                hours.setText(restaurant.getJSONArray("hours").toString());
                pin.setImageResource(R.drawable.ic_action_newpin);
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
                //get lat,lng
                //Start chose marker dialog
            }
        });
    }



}

