package com.restaurantapp.phoneapp.restaurantpinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by fix on 17/11/2014.
 */

    public class HashMapAdapter extends BaseAdapter {

        protected java.util.HashMap<String, String> users = new java.util.HashMap<String, String>();
        protected String[] keys;

        public HashMapAdapter(java.util.HashMap<String, String> data){
            users  = data;
            keys = users.keySet().toArray(new String[data.size()]);
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public String getItem(int position) {
            return users.get(keys[position]);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            String key = keys[pos];
            String Value = getItem(pos).toString();

            //do view stuff here
            if(convertView==null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_friends, parent, false);

            //Use list_friends format, hide delete button
            ImageView delete = (ImageView)convertView.findViewById(R.id.delete);
            TextView email = (TextView)convertView.findViewById(R.id.txtSub);
            TextView username = (TextView)convertView.findViewById(R.id.txtTitle);

            delete.setVisibility(View.INVISIBLE);
            email.setText(users.get(keys[pos]));
            username.setText(keys[pos]);

            return convertView;
        }
    }

