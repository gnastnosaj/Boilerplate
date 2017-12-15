package com.github.gnastnosaj.boilerplate.push.receiver;

import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by jasontsang on 12/15/17.
 */

public class PushBroadcastReceiver extends ParsePushBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        String pushData = intent.getStringExtra("data");
        if (pushData != null) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("com.parse.Data", pushData);
            broadcastIntent.setAction("com.parse.push.intent.RECEIVE");
            context.sendBroadcast(broadcastIntent);
        }
    }
}