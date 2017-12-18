package com.github.gnastnosaj.boilerplate.push.receiver;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.github.gnastnosaj.boilerplate.Boilerplate;
import com.github.gnastnosaj.boilerplate.push.R;
import com.jcmore2.freeview.FreeView;
import com.parse.ParsePushBroadcastReceiver;

import q.rorbin.badgeview.QBadgeView;

/**
 * Created by jasontsang on 12/15/17.
 */

public class DefaultPushBroadcastReceiver extends ParsePushBroadcastReceiver {

    private static int count = 0;

    private static FreeView.FreeViewListener freeViewListener;

    private Context context;

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;

        String pushData = intent.getStringExtra("data");
        if (pushData != null) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("com.parse.Data", pushData);
            broadcastIntent.setAction("com.parse.push.intent.RECEIVE");
            context.sendBroadcast(broadcastIntent);

            showFreeView();
        }
    }

    public static void setFreeViewListener(FreeView.FreeViewListener freeViewListener) {
        DefaultPushBroadcastReceiver.freeViewListener = freeViewListener;
    }

    private void showFreeView() {
        if (Boilerplate.isInBackground()) {
            if (count > 0) {
                FreeView.get().dismissFreeView();
            }
            count++;
            View contentView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.push_view, null);
            new QBadgeView(context).bindTarget(contentView.findViewById(R.id.push_icon)).setBadgeNumber(count);
            FreeView.init(context).withView(contentView).dismissOnBackground(false).showFreeView(new FreeView.FreeViewListener() {
                @Override
                public void onShow() {
                    if (freeViewListener != null) {
                        freeViewListener.onShow();
                    }
                }

                @Override
                public void onDismiss() {
                    if (freeViewListener != null) {
                        freeViewListener.onDismiss();
                    }
                }

                @Override
                public void onClick() {
                    FreeView.get().dismissFreeView();
                    count = 0;
                    if (freeViewListener != null) {
                        freeViewListener.onClick();
                    }
                }
            });
        }
    }
}