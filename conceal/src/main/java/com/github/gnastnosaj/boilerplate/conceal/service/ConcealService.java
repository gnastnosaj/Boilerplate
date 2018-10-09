package com.github.gnastnosaj.boilerplate.conceal.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.github.gnastnosaj.boilerplate.Boilerplate;

import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by jasontsang on 9/20/17.
 */

public class ConcealService extends Service {

    public final static String EXTRA_SERVICE_CLASS = "serviceClass";

    private Set<Class<? extends Service>> serviceClasses;

    private ServiceConnection guardServiceConnection;

    @Override
    public void onCreate() {
        super.onCreate();

        Boilerplate.initialize(getApplication(), new Boilerplate.Config.Builder().patch(false).fresco(false).mvc(false).build());

        Timber.d("onCreate");

        serviceClasses = new HashSet<>();

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

        if (intent != null) {
            Class<? extends Service> serviceClass = (Class<? extends Service>) intent.getSerializableExtra(EXTRA_SERVICE_CLASS);
            if (serviceClass != null) {
                serviceClasses.add(serviceClass);
            }
        }

        for (Class<? extends Service> service : serviceClasses) {
            startService(new Intent(ConcealService.this, service));
        }

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
}
