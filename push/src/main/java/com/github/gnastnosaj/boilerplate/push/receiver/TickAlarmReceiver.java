package com.github.gnastnosaj.boilerplate.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.gnastnosaj.boilerplate.push.service.PushService;

/**
 * Created by jasontsang on 12/15/17.
 */

public class TickAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, PushService.class).putExtra(PushService.EXTRA_COMMAND, PushService.COMMAND_TICK));
    }
}
