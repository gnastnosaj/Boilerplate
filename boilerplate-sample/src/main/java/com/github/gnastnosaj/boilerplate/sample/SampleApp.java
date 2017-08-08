package com.github.gnastnosaj.boilerplate.sample;

import android.app.Application;

import com.github.gnastnosaj.boilerplate.Boilerplate;

/**
 * Created by jasontsang on 4/21/17.
 */

public class SampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (Boilerplate.isInLeakCanaryAnalyzerProcess(this)) {
            return;
        }
        Boilerplate.initialize(this, new Boilerplate.Config.Builder().leakCanary(true).build());
    }
}
