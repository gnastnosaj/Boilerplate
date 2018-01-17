package com.github.gnastnosaj.boilerplate.ipc.middleware.sample;

import android.content.Context;

import com.github.gnastnosaj.boilerplate.ipc.middleware.IPCEventBus;
import com.github.gnastnosaj.boilerplate.ipc.middleware.IPCMiddleware;

/**
 * Created by jasontsang on 1/17/18.
 */

public class SampleMiddleware implements IPCMiddleware {
    @Override
    public void initialize(Context context, IPCEventBus eventBus) {

    }

    @Override
    public boolean accept(String command) {
        return true;
    }

    @Override
    public void exec(String command, Callback callback) {
        callback.tick(command);
    }
}
