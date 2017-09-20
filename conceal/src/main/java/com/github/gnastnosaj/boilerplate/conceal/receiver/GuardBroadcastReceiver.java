package com.github.gnastnosaj.boilerplate.conceal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.gnastnosaj.boilerplate.conceal.service.ConcealService;

/**
 * Created by jasontsang on 9/20/17.
 */

public class GuardBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, ConcealService.class));
    }
}
