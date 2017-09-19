package com.github.gnastnosaj.boilerplate;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;

import com.alipay.euler.andfix.patch.PatchManager;
import com.facebook.drawee.backends.pipeline.DraweeConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.github.gnastnosaj.boilerplate.log.CrashReportingTree;
import com.github.gnastnosaj.boilerplate.mvchelper.LoadViewFactory;
import com.shizhefei.mvc.ILoadViewFactory;
import com.shizhefei.mvc.MVCHelper;
import com.squareup.leakcanary.LeakCanary;
import com.wanjian.cockroach.Cockroach;

import timber.log.Timber;

/**
 * Created by jasontsang on 4/14/17.
 */

public class Boilerplate {
    public static boolean DEBUG = false;
    public static String versionName;
    public static int versionCode;

    private static Application instance;
    private static PatchManager patchManager;

    private static boolean initialized = false;
    private static boolean runtime = true;

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

    public static void initialize(Application application, Config config) {
        if (initialized) return;

        initialized = true;

        instance = application;

        DEBUG = application.getApplicationInfo() != null && (application.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        if (DEBUG && config.leakCanary) {
            if (LeakCanary.isInAnalyzerProcess(application)) return;
            LeakCanary.install(application);
        }

        if (config.log) {
            CrashReportingTree.initialize(application);
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

        if (!DEBUG && config.cockroach) {
            Cockroach.install((Thread thread, Throwable throwable) -> Timber.wtf(throwable, "CockroachException", thread));
        }

        if (config.fresco) {
            Fresco.initialize(application, config.imagePipelineConfig, config.draweeConfig);
        }

        if (config.mvc) {
            MVCHelper.setLoadViewFractory(config.loadViewFactory);
        }
    }

    public static class Config {
        private boolean log = true;

        private boolean leakCanary = false;
        private boolean cockroach = true;
        private boolean patch = true;

        private boolean fresco = true;
        private ImagePipelineConfig imagePipelineConfig;
        private DraweeConfig draweeConfig;

        private boolean mvc = true;
        private ILoadViewFactory loadViewFactory = new LoadViewFactory();

        private Config() {
        }

        public static class Builder {
            private Config config;

            public Builder() {
                config = new Config();
            }

            public Builder log(boolean enable) {
                config.log = enable;
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

            public Builder cockroach(boolean enable) {
                config.cockroach = enable;
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

    public static void runtime(Boolean enable) {
        runtime = enable;
    }

    public static void runtime(Application application) {
        if (runtime) {
            initialize(application);
        }
    }

    public static Application getInstance() {
        return instance;
    }

    public static PatchManager getPatchManager() {
        return patchManager;
    }
}