package com.restaurantapp.phoneapp.restaurantpinner;

import android.widget.SeekBar;
import android.widget.TextView;

public class SeekListener implements SeekBar.OnSeekBarChangeListener {
    private TextView current;

    public SeekListener(TextView t){
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
