package com.github.gnastnosaj.boilerplate.conceal.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.github.gnastnosaj.boilerplate.Boilerplate;

import java.util.List;

import timber.log.Timber;

import static android.content.pm.PackageManager.GET_SERVICES;

/**
 * Created by jasontsang on 9/20/17.
 */

public class ConcealService extends Service {

    private ServiceConnection guardServiceConnection;

    @Override
    public void onCreate() {
        super.onCreate();

        Boilerplate.initialize(getApplication(), new Boilerplate.Config.Builder().patch(false).fresco(false).mvc(false).build());

        Timber.d("onCreate");

        guardServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Timber.d("onServiceConnected");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Timber.d("onServiceDisconnected");

                startService(new Intent(ConcealService.this, GuardService.class));
                bindService(new Intent(ConcealService.this, GuardService.class), guardServiceConnection, Context.BIND_IMPORTANT);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("onStartCommand");

        bindService(new Intent(this, GuardService.class), guardServiceConnection, Context.BIND_IMPORTANT);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isInServiceProcess(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), GET_SERVICES);
        } catch (Exception e) {
            return false;
        }
        String mainProcess = packageInfo.applicationInfo.processName;

        ComponentName component = new ComponentName(context, ConcealService.class);
        ServiceInfo serviceInfo;
        try {
            serviceInfo = packageManager.getServiceInfo(component, 0);
        } catch (PackageManager.NameNotFoundException ignored) {
            // Service is disabled.
            return false;
        }

        if (serviceInfo.processName.equals(mainProcess)) {
            // Technically we are in the service process, but we're not in the service dedicated process.
            return false;
        }

        int myPid = android.os.Process.myPid();
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningAppProcessInfo myProcess = null;
        List<ActivityManager.RunningAppProcessInfo> runningProcesses =
                activityManager.getRunningAppProcesses();
        if (runningProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo process : runningProcesses) {
                if (process.pid == myPid) {
                    myProcess = process;
                    break;
                }
            }
        }
        if (myProcess == null) {
            return false;
        }

        return myProcess.processName.equals(serviceInfo.processName);
    }
}
