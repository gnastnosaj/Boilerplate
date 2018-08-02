package com.github.gnastnosaj.boilerplate;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alipay.euler.andfix.patch.PatchManager;
import com.facebook.drawee.backends.pipeline.DraweeConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.github.gnastnosaj.boilerplate.event.ActivityLifecycleEvent;
import com.github.gnastnosaj.boilerplate.mvchelper.LoadViewFactory;
import com.github.gnastnosaj.boilerplate.rxbus.RxBus;
import com.shizhefei.mvc.ILoadViewFactory;
import com.shizhefei.mvc.MVCHelper;
import com.squareup.leakcanary.LeakCanary;
import com.wanjian.cockroach.Cockroach;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import okio.BufferedSource;
import okio.Okio;
import timber.log.Timber;

/**
 * Created by jasontsang on 4/14/17.
 */

public class Boilerplate {
    public static boolean DEBUG;
    public static String versionName;
    public static int versionCode;

    private static Application instance;
    private static PatchManager patchManager;

    private static boolean runtime = true;
    private static boolean initialized;
    private static boolean inBackground;

    public static void initialize(Application application) {
        initialize(application, new Config());
    }

    @Deprecated
    public static void initialize(Application application, @Nullable ImagePipelineConfig imagePipelineConfig) {
        initialize(application, imagePipelineConfig, null);
    }

    @Deprecated
    public static void initialize(Application application, @Nullable ImagePipelineConfig imagePipelineConfig,
                                  @Nullable DraweeConfig draweeConfig) {
        initialize(application, new Config.Builder().setImagePipelineConfig(imagePipelineConfig).setDraweeConfig(draweeConfig).build());
    }

    public static synchronized boolean initialize(Application application, Config config) {
        if (initialized) return false;

        instance = application;

        DEBUG = application.getApplicationInfo() != null && (application.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        if (config.leakCanary) {
            if (LeakCanary.isInAnalyzerProcess(application)) {
                return false;
            }
            LeakCanary.install(application);
        }

        Timber.plant(config.logger == null ? new Timber.DebugTree() : new Timber.DebugTree() {
            @Override
            protected void log(int priority, String tag, @NonNull String message, Throwable t) {
                config.logger.log(priority, tag, message, t);
            }
        });

        if (config.logcat != null) {
            Flowable
                    .defer(() -> {
                        Runtime.getRuntime().exec("logcat -c");
                        return Flowable
                                .<Process>create(emitter -> {
                                    Process process = Runtime.getRuntime().exec("logcat -e " + application.getPackageName());
                                    emitter.onNext(process);
                                    emitter.onComplete();
                                }, BackpressureStrategy.MISSING)
                                .delay(1, TimeUnit.SECONDS)
                                .observeOn(Schedulers.io())
                                .map(process -> {
                                    try {
                                        process.exitValue();
                                        Timber.d("logcat | grep %s", application.getPackageName());
                                        return Runtime.getRuntime().exec("logcat | grep " + application.getPackageName());
                                    } catch (Throwable throwable) {
                                        Timber.d("logcat -e %s", application.getPackageName());
                                        return process;
                                    }
                                })
                                .flatMap(process ->
                                        Flowable
                                                .<String>create(emitter -> {
                                                    BufferedSource bufferedSource = Okio.buffer(Okio.source(process.getInputStream()));
                                                    while (true) {
                                                        if (emitter.requested() == 0) {
                                                            Thread.sleep(500);
                                                        } else {
                                                            String message = bufferedSource.readUtf8Line();
                                                            if (message != null) {
                                                                emitter.onNext(message);
                                                            }
                                                        }
                                                    }
                                                }, BackpressureStrategy.BUFFER)
                                                .doOnError(throwable -> process.destroy())
                                )
                                .doOnError(throwable -> Timber.e(throwable))
                                .retry();
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(message -> config.logcat.log(message), throwable -> Timber.e(throwable));
        }

        try {
            versionName = application.getPackageManager().getPackageInfo(application.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName;
            versionCode = application.getPackageManager().getPackageInfo(application.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e, "Boilerplateh initialize Exception");
        }

        if (config.patch) {
            patchManager = new PatchManager(application);
            patchManager.init(versionName);
            patchManager.loadPatch();
        }

        if (config.cockroach) {
            Cockroach.install((Thread thread, Throwable throwable) -> {
                Timber.wtf(throwable, "Cockroach Exception");
                for (Thread.UncaughtExceptionHandler uncaughtExceptionHandler : config.uncaughtExceptionHandlerList) {
                    uncaughtExceptionHandler.uncaughtException(thread, throwable);
                }
            });
        }

        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof UndeliverableException) {
                Timber.w(e, "Undeliverable exception received, not sure what to do");
            } else {
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        });

        if (config.fresco) {
            Fresco.initialize(application, config.imagePipelineConfig, config.draweeConfig);
        }

        if (config.mvc) {
            MVCHelper.setLoadViewFractory(config.loadViewFactory);
        }

        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                Timber.d("onActivityCreated:%s", activity);
                RxBus.getInstance().post(ActivityLifecycleEvent.class, new ActivityLifecycleEvent(ActivityLifecycleEvent.onActivityCreated, activity, bundle));
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Timber.d("onActivityStarted:%s", activity);
                inBackground = false;
                RxBus.getInstance().post(ActivityLifecycleEvent.class, new ActivityLifecycleEvent(ActivityLifecycleEvent.onActivityStarted, activity, null));
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Timber.d("onActivityResumed:%s", activity);
                RxBus.getInstance().post(ActivityLifecycleEvent.class, new ActivityLifecycleEvent(ActivityLifecycleEvent.onActivityResumed, activity, null));
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Timber.d("onActivityPaused:%s", activity);
                RxBus.getInstance().post(ActivityLifecycleEvent.class, new ActivityLifecycleEvent(ActivityLifecycleEvent.onActivityPaused, activity, null));
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Timber.d("onActivityStopped:%s", activity);
                inBackground = true;
                RxBus.getInstance().post(ActivityLifecycleEvent.class, new ActivityLifecycleEvent(ActivityLifecycleEvent.onActivityStopped, activity, null));
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                Timber.d("onActivitySaveInstanceState:%s", activity);
                RxBus.getInstance().post(ActivityLifecycleEvent.class, new ActivityLifecycleEvent(ActivityLifecycleEvent.onActivitySaveInstanceState, activity, bundle));
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Timber.d("onActivityDestroyed:%s", activity);
                RxBus.getInstance().post(ActivityLifecycleEvent.class, new ActivityLifecycleEvent(ActivityLifecycleEvent.onActivityDestroyed, activity, null));
            }
        });

        initialized = true;

        return true;
    }

    public static class Config {
        private Logger logger;
        private Logcat logcat;

        private boolean leakCanary = false;
        private boolean patch = false;
        private boolean cockroach = false;
        private List<Thread.UncaughtExceptionHandler> uncaughtExceptionHandlerList;

        private boolean fresco = true;
        private ImagePipelineConfig imagePipelineConfig;
        private DraweeConfig draweeConfig;

        private boolean mvc = true;
        private ILoadViewFactory loadViewFactory = new LoadViewFactory();

        private Config() {
        }

        public static interface Logger {
            void log(int priority, String tag, @NonNull String message, Throwable t);
        }

        public static interface Logcat {
            void log(@NonNull String message);
        }

        public static class Builder {
            private Config config;

            public Builder() {
                config = new Config();
            }

            public Builder logger(Logger logger) {
                config.logger = logger;
                return this;
            }

            public Builder logcat(Logcat logcat) {
                config.logcat = logcat;
                return this;
            }

            public Builder leakCanary(boolean enable) {
                config.leakCanary = enable;
                return this;
            }

            public Builder patch(boolean enable) {
                config.patch = enable;
                return this;
            }

            public Builder cockroach(boolean enable) {
                config.cockroach = enable;
                return this;
            }

            public Builder addUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
                if (config.uncaughtExceptionHandlerList == null) {
                    config.uncaughtExceptionHandlerList = new ArrayList<>();
                }
                config.uncaughtExceptionHandlerList.add(uncaughtExceptionHandler);
                return this;
            }

            public Builder fresco(boolean enable) {
                config.fresco = enable;
                return this;
            }

            public Builder setImagePipelineConfig(ImagePipelineConfig imagePipelineConfig) {
                config.imagePipelineConfig = imagePipelineConfig;
                return this;
            }

            public Builder setDraweeConfig(DraweeConfig draweeConfig) {
                config.draweeConfig = draweeConfig;
                return this;
            }

            public Builder mvc(boolean enable) {
                config.mvc = enable;
                return this;
            }

            public Builder mvc(ILoadViewFactory loadViewFactory) {
                config.loadViewFactory = loadViewFactory;
                return this;
            }

            public Config build() {
                return config;
            }
        }
    }

    public static Application getInstance() {
        return instance;
    }

    public static PatchManager getPatchManager() {
        return patchManager;
    }

    public static void runtime(Boolean enable) {
        runtime = enable;
    }

    public static void runtime(Application application) {
        if (runtime) {
            initialize(application);
        }
    }

    public static boolean isInBackground() {
        return inBackground;
    }
}