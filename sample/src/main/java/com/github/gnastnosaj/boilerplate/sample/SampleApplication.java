package com.github.gnastnosaj.boilerplate.sample;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.github.gnastnosaj.boilerplate.Boilerplate;
import com.jiongbull.jlog.Logger;
import com.jiongbull.jlog.constant.LogLevel;
import com.jiongbull.jlog.constant.LogSegment;
import com.jiongbull.jlog.util.LogUtils;

import java.io.File;
import java.util.Arrays;

import timber.log.Timber;

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        final Logger logger = Logger.Builder.newBuilder(this, "GGMP")
                .setLogDir(getPackageName() + File.separator + "log")
                .setLogLevelsForFile(Arrays.asList(LogLevel.VERBOSE, LogLevel.DEBUG, LogLevel.INFO, LogLevel.JSON, LogLevel.WARN, LogLevel.ERROR, LogLevel.WTF))
                .setLogSegment(LogSegment.ONE_HOUR)
                .setPackagedLevel(7)
                .build();

        Boilerplate.initialize(this,
                new Boilerplate.Config.Builder()
                        .fresco(false)
                        .mvc(false)
                        .cockroach(true)
                        .addUncaughtExceptionHandler((t, e) -> Timber.e(e))
                        .logger((priority, tag, message, t) -> {
                            switch (priority) {
                                case Log.VERBOSE:
                                    logger.v(tag, message);
                                    break;
                                case Log.DEBUG:
                                    logger.d(tag, message);
                                    break;
                                case Log.INFO:
                                    logger.i(tag, message);
                                    break;
                                case Log.WARN:
                                    logger.w(tag, message);
                                    break;
                                case Log.ERROR:
                                    logger.e(tag, t, message);
                                    break;
                                case Log.ASSERT:
                                    logger.wtf(tag, t, message);
                                    break;
                            }
                        })
                        .logcat(message -> {
                            String dirPath = LogUtils.genDirPath(logger.getLogDir());
                            String fileName = LogUtils.genFileName(logger.getLogPrefix(), logger.getLogSegment(), logger.getZoneOffset());
                            LogUtils.write(this, dirPath, fileName, message + "\n");
                        })
                        .build()
        );
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Boilerplate.runtime(false);
    }
}
