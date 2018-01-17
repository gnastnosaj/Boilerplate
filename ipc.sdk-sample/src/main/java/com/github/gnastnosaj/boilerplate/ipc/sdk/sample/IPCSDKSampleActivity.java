package com.github.gnastnosaj.boilerplate.ipc.sdk.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.github.gnastnosaj.boilerplate.ipc.sdk.IPCSDK;
import com.github.gnastnosaj.boilerplate.ui.activity.BaseActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

/**
 * Created by jasontsang on 1/17/18.
 */

public class IPCSDKSampleActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ipc_sdk_sample);

        findViewById(R.id.exec).setOnClickListener(v ->
                IPCSDK.getInstance()
                        .exec("ipc sample command")
                        .compose(bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(
                                tick -> Toast.makeText(IPCSDKSampleActivity.this, tick, Toast.LENGTH_SHORT).show(),
                                throwable -> Toast.makeText(IPCSDKSampleActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show()
                        )
        );
    }
}
