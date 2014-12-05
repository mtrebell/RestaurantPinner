package com.restaurantapp.phoneapp.restaurantpinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class PinAdapter extends ArrayAdapter<Pin> {

    public PinAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
    }

    public PinAdapter(Context context, int layoutResourceId, ArrayList<Pin> data) {
        super(context, layoutResourceId, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Pin pin = getItem(position);

        if(convertView==null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item_row, parent, false);
        }

        ImageView img = (ImageView)convertView.findViewById(R.id.imgIcon);
        TextView text = (TextView)convertView.findViewById(R.id.txtTitle);

        //SET ICON ACORDING TO PIN TYPE
        img.setImageResource(R.drawable.ic_action_newpin);

        text.setText(pin.name);

        return convertView;
    }
}

