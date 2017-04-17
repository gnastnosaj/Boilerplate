package com.github.gnastnosaj.boilerplate.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.githang.statusbar.StatusBarCompat;
import com.github.gnastnosaj.boilerplate.R;
import com.github.gnastnosaj.boilerplate.rxbus.RxBus;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import mehdi.sakout.dynamicbox.DynamicBox;
import timber.log.Timber;

/**
 * Created by jasontsang on 12/30/15.
 */
public class BaseActivity extends AppCompatActivity {
    public final static String DYNAMIC_BOX_AV_BALLPULSE = "_BallPulse";
    public final static String DYNAMIC_BOX_AV_BALLGRIDPULSE = "_BallGridPulse";
    public final static String DYNAMIC_BOX_AV_BALLSPINFADELOADER = "_BallSpinFadeLoader";
    public final static String DYNAMIC_BOX_AV_LINESCALEPARTY = "_LineScaleParty";
    public final static String DYNAMIC_BOX_AV_PACMAN = "_Pacman";

    public final static String DYNAMIC_BOX_MK_SHARINGAN = "_Sharingan";
    public final static String DYNAMIC_BOX_MK_TWINFISHESSPINNER = "_TwinFishesSpinner";
    public final static String DYNAMIC_BOX_MK_CLASSICSPINNER = "_ClassicSpinner";
    public final static String DYNAMIC_BOX_MK_LINESPINNER = "_LineSpinner";
    public final static String DYNAMIC_BOX_MK_FISHSPINNER = "_FishSpinner";
    public final static String DYNAMIC_BOX_MK_PHONEWAVE = "_PhoneWave";
    public final static String DYNAMIC_BOX_MK_THREEPULSE = "_ThreePulse";
    public final static String DYNAMIC_BOX_MK_FOURPULSE = "_FourPulse";
    public final static String DYNAMIC_BOX_MK_FIVEPULSE = "_FivePulse";
    public final static String DYNAMIC_BOX_MK_WORM = "_Worm";
    public final static String DYNAMIC_BOX_MK_WHIRLPOOL = "_Whirlpool";
    public final static String DYNAMIC_BOX_MK_RADAR = "_Radar";

    public final static String DYNAMIC_BOX_LT_PRELOADER = "_Preloader";

    private DynamicBox dynamicBox;
    private Observable<DynamicBoxEvent> dynamicBoxObservable;

    private Disposable dynamicBoxDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new RxPermissions(this).request(Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe();

        dynamicBoxObservable = RxBus.getInstance().register(DynamicBoxEvent.class, DynamicBoxEvent.class);
        dynamicBoxDisposable = dynamicBoxObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(dynamicBoxEvent -> {
            if (dynamicBox != null) {
                switch (dynamicBoxEvent.type) {
                    case DynamicBoxEvent.TYPE_LOADING_LAYOUT:
                        dynamicBox.showLoadingLayout();
                        break;
                    case DynamicBoxEvent.TYPE_INTERNET_OFF_LAYOUT:
                        dynamicBox.showInternetOffLayout();
                        break;
                    case DynamicBoxEvent.TYPE_EXCEPTION_LAYOUT:
                        dynamicBox.showExceptionLayout();
                        break;
                    case DynamicBoxEvent.TYPE_CUSTOM_VIEW:
                        dynamicBox.showCustomView(dynamicBoxEvent.args[0]);
                        break;
                    case DynamicBoxEvent.TYPE_HIDE_ALL:
                        dynamicBox.hideAll();
                        break;
                }
            }
        }, throwable -> Timber.e(throwable, "dynamicBoxDisposable error"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dynamicBoxDisposable.dispose();
        RxBus.getInstance().unregister(DynamicBoxEvent.class, dynamicBoxObservable);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    protected void initSystemBar(int resource) {
        StatusBarCompat.setStatusBarColor(BaseActivity.this, getResources().getColor(resource));
    }

    protected void initSystemBar() {
        initSystemBar(R.color.colorPrimaryDark);
    }

    protected DynamicBox createDynamicBox(View view) {
        if (dynamicBox == null) {
            dynamicBox = new DynamicBox(this, view);
            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.ballpulse_avloading, null), DYNAMIC_BOX_AV_BALLPULSE);
            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.ballgridpulse_avloading, null), DYNAMIC_BOX_AV_BALLGRIDPULSE);
            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.ballspinfadeloader_avloading, null), DYNAMIC_BOX_AV_BALLSPINFADELOADER);
            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.linescaleparty_avloading, null), DYNAMIC_BOX_AV_LINESCALEPARTY);
            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.pacman_avloading, null), DYNAMIC_BOX_AV_PACMAN);

            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.sharingan_mkloader, null), DYNAMIC_BOX_MK_SHARINGAN);
            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.twinfishesspinner_mkloader, null), DYNAMIC_BOX_MK_TWINFISHESSPINNER);
            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.classicspinner_mkloader, null), DYNAMIC_BOX_MK_CLASSICSPINNER);
            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.linespinner_mkloader, null), DYNAMIC_BOX_MK_LINESPINNER);
            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.fishspinner_mkloader, null), DYNAMIC_BOX_MK_FISHSPINNER);
            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.phonewave_mkloader, null), DYNAMIC_BOX_MK_PHONEWAVE);
            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.threepulse_mkloader, null), DYNAMIC_BOX_MK_THREEPULSE);
            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.fourpulse_mkloader, null), DYNAMIC_BOX_MK_FOURPULSE);
            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.fivepulse_mkloader, null), DYNAMIC_BOX_MK_FIVEPULSE);
            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.worm_mkloader, null), DYNAMIC_BOX_MK_WORM);
            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.whirlpool_mkloader, null), DYNAMIC_BOX_MK_WHIRLPOOL);
            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.radar_mkloader, null), DYNAMIC_BOX_MK_RADAR);

            dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.preloader_lottie, null), DYNAMIC_BOX_LT_PRELOADER);
        }
        return dynamicBox;
    }

    protected DynamicBox createDynamicBox() {
        return createDynamicBox(findViewById(android.R.id.content));
    }

    public static void showDynamicBoxLoadingLayout() {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_LOADING_LAYOUT));
    }

    public static void showDynamicBoxInternetOffLayout() {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_INTERNET_OFF_LAYOUT));
    }

    public static void showDynamicBoxExceptionLayout() {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_EXCEPTION_LAYOUT));
    }

    public static void showDynamicBoxCustomView(String tag) {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_CUSTOM_VIEW, new String[]{tag}));
    }

    public static void dismissDynamicBox() {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_HIDE_ALL));
    }

    private static class DynamicBoxEvent {
        final static int TYPE_LOADING_LAYOUT = 0;
        final static int TYPE_INTERNET_OFF_LAYOUT = 1;
        final static int TYPE_EXCEPTION_LAYOUT = 2;
        final static int TYPE_CUSTOM_VIEW = 3;
        final static int TYPE_HIDE_ALL = 4;

        int type;
        String[] args;

        DynamicBoxEvent(int type) {
            this.type = type;
        }

        DynamicBoxEvent(int type, String[] args) {
            this.type = type;
            this.args = args;
        }
    }
}
