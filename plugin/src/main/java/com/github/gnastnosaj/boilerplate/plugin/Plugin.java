package com.github.gnastnosaj.boilerplate.plugin;

import android.app.Application;

import com.morgoo.droidplugin.PluginHelper;

/**
 * Created by jasontsang on 1/29/18.
 */

public class Plugin {
    public static void initialize(Application application) {
        PluginHelper.getInstance().applicationOnCreate(application);
    }
}
