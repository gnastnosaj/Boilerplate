package com.github.gnastnosaj.boilerplate.ipc;

import com.github.gnastnosaj.boilerplate.ipc.IPCCallback;

interface IPC {
    void exec(String command, IPCCallback callback);

    void subscribe(IPCCallback callback);

    void dispose(IPCCallback callback);

    void register(String tag, IPCCallback callback);

    void unregister(String tag, IPCCallback callback);
}
