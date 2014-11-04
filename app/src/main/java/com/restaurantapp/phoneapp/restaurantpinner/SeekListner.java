package com.restaurantapp.phoneapp.restaurantpinner;

import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by fix on 02/11/2014.
 */
public class SeekListner implements SeekBar.OnSeekBarChangeListener {
    private TextView current;

    public SeekListner(TextView t){
        current =t;
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        current.setText("Within " + progress + " kms");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
