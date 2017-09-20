package com.github.gnastnosaj.boilerplate.sample;

import android.app.Application;
import android.content.Context;

/**
 * Created by jasontsang on 4/21/17.
 */

public class SampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        Boilerplate.initialize(this, new Boilerplate.Config.Builder().leakCanary(true).build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        Boilerplate.runtime(false);
    }
}
