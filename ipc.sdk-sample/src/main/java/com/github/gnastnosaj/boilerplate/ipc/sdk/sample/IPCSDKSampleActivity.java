package com.github.gnastnosaj.boilerplate.ipc.sdk.sample;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.github.gnastnosaj.boilerplate.ipc.aidl.IPCCallback;
import com.github.gnastnosaj.boilerplate.ipc.aidl.IPCException;
import com.github.gnastnosaj.boilerplate.ipc.sdk.IPCSDK;
import com.github.gnastnosaj.boilerplate.ui.activity.BaseActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jasontsang on 1/17/18.
 */

public class IPCSDKSampleActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ipc_sdk_sample);

        Observable.just(this)
                .subscribeOn(Schedulers.io())
                .subscribe(ipcsdkSampleActivity -> {
                    IPCSDK.getInstance().register("sample", new IPCCallback.Stub() {
                        @Override
                        public void onNext(String next) throws RemoteException {
                            Observable.just(next)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            tick -> Toast.makeText(IPCSDKSampleActivity.this, "register 1:" + tick, Toast.LENGTH_SHORT).show(),
                                            throwable -> Toast.makeText(IPCSDKSampleActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        }

                        @Override
                        public void onComplete() throws RemoteException {

                        }

                        @Override
                        public void onError(IPCException e) throws RemoteException {

                        }
                    });

                    IPCSDK.getInstance().register("sample", new IPCCallback.Stub() {
                        @Override
                        public void onNext(String next) throws RemoteException {
                            Observable.just(next)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            tick -> Toast.makeText(IPCSDKSampleActivity.this, "register 2:" + tick, Toast.LENGTH_SHORT).show(),
                                            throwable -> Toast.makeText(IPCSDKSampleActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        }

                        @Override
                        public void onComplete() throws RemoteException {

                        }

                        @Override
                        public void onError(IPCException e) throws RemoteException {

                        }
                    });
                });

        findViewById(R.id.exec).setOnClickListener(v ->
                IPCSDK.getInstance()
                        .exec("com.github.gnastnosaj.boilerplate.ipc.middleware.sample", "ipc sample command")
                        .compose(bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(
                                tick -> Toast.makeText(IPCSDKSampleActivity.this, tick, Toast.LENGTH_SHORT).show(),
                                throwable -> Toast.makeText(IPCSDKSampleActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()
                        )
        );
    }
}
