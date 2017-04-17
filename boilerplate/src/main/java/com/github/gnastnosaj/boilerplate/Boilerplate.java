package com.github.gnastnosaj.boilerplate;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import com.github.gnastnosaj.boilerplate.log.CrashReportingTree;
import com.wanjian.cockroach.Cockroach;

import timber.log.Timber;

/**
 * Created by jasontsang on 4/14/17.
 */

public class Boilerplate {
    public static boolean DEBUG = false;

    private static Application instance;

    public static void initialize(Application application) {
        instance = application;

        DEBUG = application.getApplicationInfo() != null && (application.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        CrashReportingTree.initialize(application);

        if(!DEBUG) {
            Cockroach.install((Thread thread, Throwable throwable) -> Timber.wtf(throwable, "CockroachException", thread));
        }
    }

    public static Application getInstance() {
        return instance;
    }
}