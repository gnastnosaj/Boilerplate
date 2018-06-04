package com.github.gnastnosaj.boilerplate.push.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

import json2notification.Json2Notification;
import timber.log.Timber;

/**
 * Created by Jason on 8/10/2015.
 */
public class Json2NotificationPushBroadcastReceiver extends ParsePushBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        try {
            String json = intent.getStringExtra("com.parse.Data");
            Notification notification = Json2Notification.from(context).with(json).notification();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify((int) System.currentTimeMillis(), notification);
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
