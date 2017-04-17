package com.github.gnastnosaj.boilerplate;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.github.gnastnosaj.boilerplate.log.CrashReportingTree;
import com.wanjian.cockroach.Cockroach;

import timber.log.Timber;

/**
 * Created by jasontsang on 4/14/17.
 */

public class Boilerplate {
    public static boolean DEBUG = false;

    public static void initialize(Context context) {
        DEBUG = context.getApplicationInfo() != null && (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        CrashReportingTree.initialize(context);

        if(!DEBUG) {
            Cockroach.install((Thread thread, Throwable throwable) -> Timber.wtf(throwable, "CockroachException", thread));
        }
    }
}