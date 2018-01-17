package com.github.gnastnosaj.boilerplate.ipc;

interface IPCCallback {
    void onNext(String next);
    void onComplete();
}
