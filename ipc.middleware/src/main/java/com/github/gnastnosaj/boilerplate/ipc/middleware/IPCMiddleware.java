package com.github.gnastnosaj.boilerplate.ipc.middleware;

import android.content.Context;

/**
 * Created by jasontsang on 1/17/18.
 */

public interface IPCMiddleware {
    void initialize(Context context, IPCEventBus eventBus);

    boolean accept(String command);

    void exec(String command, Callback callback);

    interface Callback {
        void tick(String tick);
    }
}
