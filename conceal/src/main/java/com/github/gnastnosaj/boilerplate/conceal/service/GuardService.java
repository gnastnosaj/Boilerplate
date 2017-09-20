package com.github.gnastnosaj.boilerplate.conceal.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;

import timber.log.Timber;

/**
 * Created by jasontsang on 9/20/17.
 */

public class GuardService extends Service {

    private ServiceConnection concealServiceConnection;

    @Override
    public void onCreate() {
        super.onCreate();

        concealServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Timber.d("onServiceConnected");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Timber.d("onServiceDisconnected");

                startService(new Intent(GuardService.this, ConcealService.class));
                bindService(new Intent(GuardService.this, ConcealService.class), concealServiceConnection, Context.BIND_IMPORTANT);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("onStartCommand");

        bindService(new Intent(this, ConcealService.class), concealServiceConnection, Context.BIND_IMPORTANT);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
