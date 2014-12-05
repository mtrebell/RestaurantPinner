package com.restaurantapp.phoneapp.restaurantpinner;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class Pin implements Parcelable {
    public double lat,lng;
    public String uuid,name,address;
    public ArrayList<String> types;
    public MarkerOptions marker;

    public Pin(Parcel p){
        lat = p.readDouble();
        lng = p.readDouble();

        types  = new ArrayList<String>();
        p.readList(types,String.class.getClassLoader());

        uuid = p.readString();
        name = p.readString();
        address = p.readString();

        marker = p.readParcelable(MarkerOptions.class.getClassLoader());
    }

    public Pin(String uuid,String name,String address,String type,double lat,double lng){
        this.uuid=uuid;
        this.name=name;
        this.address=address;
        this.lat=lat;
        this.lng=lng;
        types = new ArrayList<String>();
        types.add(type);

        LatLng latlng = new LatLng(lat,lng);
        marker = new MarkerOptions()
                .position(latlng)
                .title(name)
                .snippet(uuid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
        parcel.writeList(types);
        parcel.writeString(uuid);
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeParcelable(marker,i);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Pin createFromParcel(Parcel in) {
            return new Pin(in);
        }

        public Pin[] newArray(int size) {
            return new Pin[size];
        }
    };
}
