package com.github.gnastnosaj.boilerplate.sample;

import android.os.Bundle;

import com.github.gnastnosaj.boilerplate.ui.activity.BaseActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by jasontsang on 4/21/17.
 */

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createDynamicBox();

        Observable.timer(3, TimeUnit.SECONDS).compose(this.<Long>bindToLifecycle()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
                showDynamicBoxCustomView(DYNAMIC_BOX_MK_CLASSICSPINNER, MainActivity.this);
            }
        });
    }
}
