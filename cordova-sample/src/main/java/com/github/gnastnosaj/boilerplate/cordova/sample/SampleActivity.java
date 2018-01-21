package com.github.gnastnosaj.boilerplate.cordova.sample;

import android.os.Bundle;

import com.github.gnastnosaj.boilerplate.cordova.ext.HackyCordovaActivity;

/**
 * Created by jasontsang on 1/19/18.
 */

public class SampleActivity extends HackyCordovaActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.init();

        loadUrl("https://github.com/gnastnosaj");
    }

    @Override
    protected void initViews() {
        createDefaultViews();
    }
}
