package com.github.gnastnosaj.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.gnastnosaj.boilerplate.ui.activity.BaseActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by jasontsang on 4/14/17.
 */

public class DemoActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.e("fuck you");

        Observable.timer(3, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long aLong) throws Exception {
                showDynamicBoxExceptionLayout();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getDynamicBox().setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DemoActivity.this, "哈哈哈哈", Toast.LENGTH_SHORT).show();
                dimissDynamicBox();
            }
        });
    }
}
