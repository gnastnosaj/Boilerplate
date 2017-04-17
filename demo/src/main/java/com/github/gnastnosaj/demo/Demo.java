package com.github.gnastnosaj.demo;

import android.app.Application;

import com.github.gnastnosaj.boilerplate.Boilerplate;

/**
 * Created by jasontsang on 4/14/17.
 */

public class Demo extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Boilerplate.initialize(this);
    }
}
