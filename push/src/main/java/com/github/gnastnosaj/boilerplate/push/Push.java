package com.github.gnastnosaj.boilerplate.push;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.gnastnosaj.boilerplate.conceal.Conceal;
import com.github.gnastnosaj.boilerplate.push.service.PushService;
import com.parse.Parse;

import timber.log.Timber;

/**
 * Created by jasontsang on 12/15/17.
 */

public class Push {
    public final static String DEFAULT_PRE_NAME = "defaultAccount";
    public final static String UUID = "uuid";
    public final static String SERVER_IP = "serverIp";
    public final static String SERVER_PORT = "serverPort";

    private static Context context;
    private static boolean initialized;

    private static String uuid;
    private static String serverIp;
    private static String serverPort;

    public static void initialize(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DEFAULT_PRE_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(UUID)) {
            initialize(context, sharedPreferences.getString(UUID, null), sharedPreferences.getString(SERVER_IP, null), sharedPreferences.getString(SERVER_PORT, null));
        }
    }

    public static synchronized void initialize(Context context, String uuid, String serverIP, String serverPort) {
        if (!initialized) {
            Parse.initialize(context);

            Push.context = context;

            cache(uuid, serverIP, serverPort);

            Conceal.conceal(context, PushService.class);

            initialized = true;
        } else {
            Timber.w("Push already initialized");
        }
    }

    private static void cache(String uuid, String serverIP, String serverPort) {
        Push.uuid = uuid;
        Push.serverIp = serverIP;
        Push.serverPort = serverPort;

        SharedPreferences sharedPreferences = context.getSharedPreferences(DEFAULT_PRE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(UUID, uuid);
        editor.putString(SERVER_IP, serverIP);
        editor.putString(SERVER_PORT, serverPort);
        editor.apply();
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static String getUuid() {
        return uuid;
    }

    public static String getServerIp() {
        return serverIp;
    }

    public static String getServerPort() {
        return serverPort;
    }
}