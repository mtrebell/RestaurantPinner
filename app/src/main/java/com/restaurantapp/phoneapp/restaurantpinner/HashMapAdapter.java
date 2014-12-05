package com.restaurantapp.phoneapp.restaurantpinner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
            if(convertView==null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);

            TextView sub = (TextView)convertView.findViewById(R.id.txtSub);
            TextView title = (TextView)convertView.findViewById(R.id.txtTitle);

            sub.setText(users.get(keys[pos]));
            title.setText(keys[pos]);

            return convertView;
        }
    }

