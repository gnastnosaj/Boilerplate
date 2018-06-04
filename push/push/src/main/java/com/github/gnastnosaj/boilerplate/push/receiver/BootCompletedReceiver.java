package com.github.gnastnosaj.boilerplate.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.gnastnosaj.boilerplate.conceal.Conceal;
import com.github.gnastnosaj.boilerplate.push.service.PushService;

/**
 * Created by jasontsang on 12/15/17.
 */

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Conceal.conceal(context, PushService.class);
    }
}