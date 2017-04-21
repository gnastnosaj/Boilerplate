package com.github.gnastnosaj.boilerplate;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.alipay.euler.andfix.patch.PatchManager;
import com.github.gnastnosaj.boilerplate.log.CrashReportingTree;
import com.wanjian.cockroach.Cockroach;

import timber.log.Timber;

/**
 * Created by jasontsang on 4/14/17.
 */

public class Boilerplate {
    public static boolean DEBUG = false;
    public static String versionName;
    public static int versionCode;

    private static Application instance;
    private static PatchManager patchManager;

    public static void initialize(Application application) {
        instance = application;

        DEBUG = application.getApplicationInfo() != null && (application.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        CrashReportingTree.initialize(application);

        try {
            versionName = application.getPackageManager().getPackageInfo(application.getPackageName(), PackageInfo.INSTALL_LOCATION_AUTO).versionName;
            versionCode = application.getPackageManager().getPackageInfo(application.getPackageName(), PackageInfo.INSTALL_LOCATION_AUTO).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e, "Boilerplateh initialize Exception");
        }

        patchManager = new PatchManager(application);
        patchManager.init(versionName);

        if (!DEBUG) {
            Cockroach.install((Thread thread, Throwable throwable) -> Timber.wtf(throwable, "CockroachException", thread));
        }
    }

    public static Application getInstance() {
        return instance;
    }

    public static PatchManager getPatchManager() {
        return patchManager;
    }
}