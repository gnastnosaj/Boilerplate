package com.github.gnastnosaj.boilerplate.ipc.sdk.sample;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.gnastnosaj.boilerplate.ipc.IPCCallback;
import com.github.gnastnosaj.boilerplate.ipc.sdk.IPCSDK;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jasontsang on 1/17/18.
 */

public class IPCSDKSampleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ipc_sdk_sample);

        findViewById(R.id.exec).setOnClickListener(v ->
                Observable.just(v)
                        .subscribeOn(Schedulers.io())
                        .subscribe(view -> IPCSDK.getInstance().exec("ipc sample command", new IPCCallback.Stub() {
                            @Override
                            public void tick(String tick) throws RemoteException {
                                Observable.just(tick)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(t -> Toast.makeText(IPCSDKSampleActivity.this, tick, Toast.LENGTH_SHORT).show());
                            }
                        }), throwable -> Observable.just(throwable)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(t -> Toast.makeText(IPCSDKSampleActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()))
        );
    }
}
