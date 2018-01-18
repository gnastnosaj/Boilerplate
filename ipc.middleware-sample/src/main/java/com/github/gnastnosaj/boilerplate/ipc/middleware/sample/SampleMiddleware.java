package com.github.gnastnosaj.boilerplate.ipc.middleware.sample;

import android.content.Context;

import com.github.gnastnosaj.boilerplate.ipc.middleware.IPCEvent;
import com.github.gnastnosaj.boilerplate.ipc.middleware.IPCEventBus;
import com.github.gnastnosaj.boilerplate.ipc.middleware.IPCMiddleware;
import com.github.gnastnosaj.boilerplate.ipc.middleware.IPCMiddlewareCallback;

/**
 * Created by jasontsang on 1/17/18.
 */

public class SampleMiddleware implements IPCMiddleware {
    private IPCEventBus eventBus;

    @Override
    public void initialize(Context context, IPCEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public boolean accept(String scheme) {
        return scheme.equals("com.github.gnastnosaj.boilerplate.ipc.middleware.sample");
    }

    @Override
    public void exec(String data, IPCMiddlewareCallback callback) {
        callback.perform(data);

        new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            eventBus.post("sample", new IPCEvent() {
                @Override
                public String toString() {
                    return "sample ipc event";
                }
            });
        }).start();
    }
}