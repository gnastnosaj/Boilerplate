package com.github.gnastnosaj.boilerplate.log;

import android.content.Context;
import android.util.Log;

import com.github.gnastnosaj.boilerplate.Boilerplate;
import com.jiongbull.jlog.JLog;

import java.io.File;

import timber.log.Timber;

/**
 * Created by jasontsang on 12/30/15.
 */
public class CrashReportingTree extends Timber.Tree {
    public static void initialize(Context context) {
        JLog.init()
                .setDebug(Boilerplate.DEBUG)
                .setPackagedLevel(5)
                .writeToFile(true)
                .setLogDir(context.getPackageName() + File.separator + "log")
                .setTimeFormat("yyyy/MM/dd HH:mm:ss");
        Timber.plant(new CrashReportingTree());
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        switch (priority) {
            case Log.VERBOSE:
                JLog.v(tag, message);
                break;
            case Log.DEBUG:
                JLog.d(tag, message);
                break;
            case Log.INFO:
                JLog.i(tag, message);
                break;
            case Log.WARN:
                JLog.w(tag, message);
                break;
            case Log.ERROR:
                JLog.e(tag, t, message);
                break;
            case Log.ASSERT:
                JLog.wtf(tag, t, message);
                break;
        }
    }
}
