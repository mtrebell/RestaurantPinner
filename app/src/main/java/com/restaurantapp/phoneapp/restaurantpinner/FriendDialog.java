package com.restaurantapp.phoneapp.restaurantpinner;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class FriendDialog {
    HashMapAdapter adapter;
    ListView lv;
    List<String> friends;
    AlertDialog.Builder alertDialog;

    public FriendDialog(HashMap<String,String> users,Context context) {
        friends = new ArrayList<String>();
        alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View convertView = inflater.inflate(R.layout.fragment_item_list, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Choose Friend(s)");

        setButtons();

        lv = (ListView) convertView.findViewById(R.id.list);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setSelector(R.drawable.selected);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                String friend = adapter.getItem(position);
                if(friends.contains(friend))
                    friends.remove(friend);
                else
                    friends.add(friend);
            }
        });

        adapter = new HashMapAdapter(users);
        lv.setAdapter(adapter);

        alertDialog.show();
    }

    protected abstract void setButtons();

}