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
import com.shizhefei.mvc.MVCHelper;
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
        instance = application;

        DEBUG = application.getApplicationInfo() != null && (application.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        CrashReportingTree.initialize(application);

        try {
            versionName = application.getPackageManager().getPackageInfo(application.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName;
            versionCode = application.getPackageManager().getPackageInfo(application.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e, "Boilerplateh initialize Exception");
        }

        patchManager = new PatchManager(application);
        patchManager.init(versionName);
        patchManager.loadPatch();

        if (!DEBUG) {
            Cockroach.install((Thread thread, Throwable throwable) -> Timber.wtf(throwable, "CockroachException", thread));
        }

        Fresco.initialize(application, imagePipelineConfig, draweeConfig);
        MVCHelper.setLoadViewFractory(new LoadViewFactory());
    }

    public static void initialize(Application application, Config config) {
        instance = application;

        DEBUG = application.getApplicationInfo() != null && (application.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

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

        if (!DEBUG) {
            Cockroach.install((Thread thread, Throwable throwable) -> Timber.wtf(throwable, "CockroachException", thread));
        }

        if (config.fresco) {
            Fresco.initialize(application, config.imagePipelineConfig, config.draweeConfig);
        }

        if (config.mvc) {
            MVCHelper.setLoadViewFractory(new LoadViewFactory());
        }
    }

    public static class Config {
        private boolean log = true;

        private boolean fresco = true;
        private ImagePipelineConfig imagePipelineConfig;
        private DraweeConfig draweeConfig;

        private boolean patch = true;

        private boolean mvc = true;

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

            public Builder patch(boolean enable) {
                config.patch = enable;
                return this;
            }

            public Builder mvc(boolean enable) {
                config.mvc = enable;
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
}