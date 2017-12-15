package com.github.gnastnosaj.boilerplate.push.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.github.gnastnosaj.boilerplate.push.Push;
import com.github.gnastnosaj.boilerplate.push.Util;
import com.github.gnastnosaj.boilerplate.push.receiver.TickAlarmReceiver;

import org.ddpush.im.v1.client.appuser.Message;
import org.ddpush.im.v1.client.appuser.UDPClientBase;

import java.nio.ByteBuffer;

import timber.log.Timber;

/**
 * Created by jasontsang on 12/15/17.
 */

public class PushService extends Service {
    public final static String EXTRA_COMMAND = "command";

    public final static int COMMAND_RESET = 0;
    public final static int COMMAND_TICK = 1;

    private PowerManager.WakeLock wakeLock;

    private PushUdpClient pushUdpClient;

    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "OnlineService");

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TickAlarmReceiver.class);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                5 * 60 * 1000,
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int command = intent.getIntExtra(EXTRA_COMMAND, COMMAND_RESET);
            switch (command) {
                case COMMAND_TICK:
                    acquireWakeLock();
                    break;
                case COMMAND_RESET:
                    acquireWakeLock();
                    resetClient();
                    break;
            }
        } else {
            acquireWakeLock();
            resetClient();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void resetClient() {
        if (pushUdpClient != null) {
            try {
                pushUdpClient.stop();
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        if (!Push.isInitialized()) {
            Push.initialize(this);
        }
        if (Push.isInitialized()) {
            try {
                pushUdpClient = new PushUdpClient(Util.md5Byte(Push.getUuid()), 1, Push.getServerIp(), Integer.parseInt(Push.getServerPort()));
                pushUdpClient.setHeartbeatInterval(30);
                pushUdpClient.start();
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private void notify(int command, String message) {
        Intent intent = new Intent();
        intent.setAction("com.github.gnastnosaj.boilerplate.push");
        intent.putExtra("data", message);
        intent.putExtra("cmd", command);
        sendBroadcast(intent);
    }

    private void acquireWakeLock() {
        if (wakeLock != null && !wakeLock.isHeld()) {
            wakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    private class PushUdpClient extends UDPClientBase {

        public PushUdpClient(byte[] uuid, int appid, String serverAddr, int serverPort) throws Exception {
            super(uuid, appid, serverAddr, serverPort);
        }

        @Override
        public boolean hasNetworkConnection() {
            return Util.hasNetwork(PushService.this);
        }


        @Override
        public void trySystemSleep() {
            releaseWakeLock();
        }

        @Override
        public void onPushMessage(Message message) {
            if (message == null) {
                return;
            }
            if (message.getData() == null || message.getData().length == 0) {
                return;
            }
            if (message.getCmd() == 16) {// 0x10
                PushService.this.notify(16, "");
            }
            if (message.getCmd() == 17) {// 0x11
                long msg = ByteBuffer.wrap(message.getData(), 5, 8).getLong();
                PushService.this.notify(17, "" + msg);
            }
            if (message.getCmd() == 32) {// 0x20
                String str;
                try {
                    str = new String(message.getData(), 5, message.getContentLength(), "UTF-8");
                } catch (Exception e) {
                    str = Util.convert(message.getData(), 5, message.getContentLength());
                }
                PushService.this.notify(32, str);
            }
        }
    }
}
