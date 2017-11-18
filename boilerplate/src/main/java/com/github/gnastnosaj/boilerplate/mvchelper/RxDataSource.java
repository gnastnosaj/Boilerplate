package com.github.gnastnosaj.boilerplate.mvchelper;

import android.content.Context;

import com.github.gnastnosaj.boilerplate.ui.activity.BaseActivity;
import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jasontsang on 7/31/17.
 */

public abstract class RxDataSource<DATA> implements IAsyncDataSource<DATA> {

    private final static List<Hook> hooks = new ArrayList<>();

    public static void addHook(Hook hook) {
        hooks.add(hook);
    }

    private Context context;

    public RxDataSource(Context context) {
        this.context = context;
    }

    @Override
    public final RequestHandle refresh(final ResponseSender<DATA> sender) throws Exception {
        Observable<DATA> refresh = refresh();

        if (context instanceof BaseActivity) {
            refresh = refresh.compose(((BaseActivity) context).bindUntilEvent(ActivityEvent.DESTROY));
        }

        refresh = refresh.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        for(Hook hook : hooks) {
            refresh = hook.hook(context, refresh);
        }

        Disposable disposable = refresh
                .subscribe(data -> sender.sendData(data), throwable -> sender.sendError(throwable instanceof Exception ? (Exception) throwable : new Exception(throwable)));

        return new RequestHandle() {
            @Override
            public void cancle() {
                if (!disposable.isDisposed()) {
                    disposable.dispose();
                }
            }

            @Override
            public boolean isRunning() {
                return false;
            }
        };
    }

    @Override
    public final RequestHandle loadMore(ResponseSender<DATA> sender) throws Exception {
        Observable<DATA> loadMore = loadMore();

        if (context instanceof BaseActivity) {
            loadMore = loadMore.compose(((BaseActivity) context).bindUntilEvent(ActivityEvent.DESTROY));
        }

        loadMore = loadMore.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        for(Hook hook : hooks) {
            loadMore = hook.hook(context, loadMore);
        }

        Disposable disposable = loadMore
                .subscribe(data -> sender.sendData(data), throwable -> sender.sendError(throwable instanceof Exception ? (Exception) throwable : new Exception(throwable)));

        return new RequestHandle() {
            @Override
            public void cancle() {
                if (!disposable.isDisposed()) {
                    disposable.dispose();
                }
            }

            @Override
            public boolean isRunning() {
                return false;
            }
        };
    }

    public abstract Observable<DATA> refresh() throws Exception;

    public abstract Observable<DATA> loadMore() throws Exception;

    public interface Hook<DATA> {
        Observable<DATA> hook(Context context, Observable<DATA> data);
    }
}
