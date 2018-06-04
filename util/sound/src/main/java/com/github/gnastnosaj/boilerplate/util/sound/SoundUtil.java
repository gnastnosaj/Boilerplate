package com.github.gnastnosaj.boilerplate.util.sound;

import android.content.Context;

import com.huhx0015.hxaudio.audio.HXSound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jasontsang on 12/14/17.
 */

public class SoundUtil {

    private static Context context;

    private static Map<String, Integer> soundMap = new HashMap<>();

    public static void initialize(Context context, String... soundKeys) {
        SoundUtil.context = context;

        add(soundKeys);
    }

    public static void add(String... soundKeys) {
        if (soundKeys != null && soundKeys.length > 0) {
            
            List<Integer> soundResources = new ArrayList<>();

            for (String soundKey : soundKeys) {
                int soundResource = context.getResources().getIdentifier("sfx_" + soundKey, "raw", context.getPackageName());

                soundResources.add(soundResource);
                soundMap.put(soundKey, soundResource);
            }

            HXSound.load(soundResources, context);
        }
    }

    public static void play(String soundKey) {
        if (soundMap.containsKey(soundKey)) {
            HXSound.sound().load(soundMap.get(soundKey)).play(context);
        }
    }
}
