package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fix on 11/11/2014.
 */
public class MarkerAdapter extends ArrayAdapter<MarkerOptions> {

    public MarkerAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
    }

    public MarkerAdapter(Context context, int layoutResourceId,  ArrayList<MarkerOptions> data) {
        super(context, layoutResourceId, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MarkerOptions marker = getItem(position);

        if(convertView==null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item_row, parent, false);
        }

        ImageView img = (ImageView)convertView.findViewById(R.id.imgIcon);
        TextView text = (TextView)convertView.findViewById(R.id.txtTitle);

        img.setImageResource(R.drawable.ic_action_newpin);
        text.setText(marker.getTitle());

        return convertView;
    }

}

