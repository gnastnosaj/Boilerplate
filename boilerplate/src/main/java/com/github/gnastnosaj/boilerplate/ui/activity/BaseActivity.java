package com.github.gnastnosaj.boilerplate.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import com.githang.statusbar.StatusBarCompat;
import com.github.gnastnosaj.boilerplate.R;
import com.github.gnastnosaj.boilerplate.rxbus.RxBus;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import mehdi.sakout.dynamicbox.DynamicBox;
import timber.log.Timber;

/**
 * Created by jasontsang on 12/30/15.
 */
public class BaseActivity extends RxAppCompatActivity {
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

    public final static String PENDING_TRANSITION_LEFT_RIGHT = "_left_right";

    private final static Observable<DynamicBoxEvent> dynamicBoxObservable = RxBus.getInstance().register(DynamicBoxEvent.class, DynamicBoxEvent.class);

    private List<DynamicBox> dynamicBoxes = new ArrayList<>();

    private String pendingTransition = PENDING_TRANSITION_LEFT_RIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new RxPermissions(this).request(Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe();

        dynamicBoxObservable.compose(bindToLifecycle()).observeOn(AndroidSchedulers.mainThread()).subscribe(dynamicBoxEvent -> {
            if (!dynamicBoxes.isEmpty()) {
                if (dynamicBoxEvent.context != null && !dynamicBoxEvent.context.equals(this)) {
                    return;
                }
                for (DynamicBox dynamicBox : dynamicBoxes) {
                    if (dynamicBoxEvent.args != null) {
                        if (dynamicBoxEvent.args.length > 1 || dynamicBoxEvent.type != DynamicBoxEvent.TYPE_CUSTOM_VIEW) {
                            if (!Arrays.asList(dynamicBoxEvent.args).contains(dynamicBox)) {
                                continue;
                            }
                        }
                    }
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
                            dynamicBox.showCustomView((String) dynamicBoxEvent.args[0]);
                            break;
                        case DynamicBoxEvent.TYPE_HIDE_ALL:
                            dynamicBox.hideAll();
                            break;
                    }
                }
            }
        }, throwable -> Timber.e(throwable, "dynamicBoxDisposable error"));
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (!TextUtils.isEmpty(pendingTransition)) {
            if (pendingTransition.equals(PENDING_TRANSITION_LEFT_RIGHT)) {
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (!TextUtils.isEmpty(pendingTransition)) {
            if (pendingTransition.equals(PENDING_TRANSITION_LEFT_RIGHT)) {
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        }
    }

    protected void initSystemBar(int resource) {
        StatusBarCompat.setStatusBarColor(BaseActivity.this, getResources().getColor(resource));
    }

    protected void initSystemBar() {
        initSystemBar(R.color.colorPrimaryDark);
    }

    protected DynamicBox createDynamicBox(View targetView) {
        ViewGroup.LayoutParams targetViewLayoutParams = targetView.getLayoutParams();

        DynamicBox dynamicBox = new DynamicBox(this, targetView);
        dynamicBoxes.add(dynamicBox);

        if (targetView.getParent() instanceof ViewSwitcher) {
            ViewSwitcher switcher = (ViewSwitcher) targetView.getParent();
            ViewGroup.LayoutParams switcherLayoutParams = switcher.getLayoutParams();
            switcherLayoutParams.width = targetViewLayoutParams.width;
            switcherLayoutParams.height = targetViewLayoutParams.height;
            switcher.setLayoutParams(switcherLayoutParams);
        }

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

        return dynamicBox;
    }

    protected DynamicBox createDynamicBox(View targetView, String[] tags) {
        ViewGroup.LayoutParams targetViewLayoutParams = targetView.getLayoutParams();

        DynamicBox dynamicBox = new DynamicBox(this, targetView);
        dynamicBoxes.add(dynamicBox);

        if (targetView.getParent() instanceof ViewSwitcher) {
            ViewSwitcher switcher = (ViewSwitcher) targetView.getParent();
            ViewGroup.LayoutParams switcherLayoutParams = switcher.getLayoutParams();
            switcherLayoutParams.width = targetViewLayoutParams.width;
            switcherLayoutParams.height = targetViewLayoutParams.height;
            switcher.setLayoutParams(switcherLayoutParams);
        }

        if (tags != null) {
            List tagList = Arrays.asList(tags);

            if (tagList.contains(DYNAMIC_BOX_AV_BALLPULSE)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.ballpulse_avloading, null), DYNAMIC_BOX_AV_BALLPULSE);
            }
            if (tagList.contains(DYNAMIC_BOX_AV_BALLGRIDPULSE)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.ballgridpulse_avloading, null), DYNAMIC_BOX_AV_BALLGRIDPULSE);
            }
            if (tagList.contains(DYNAMIC_BOX_AV_BALLSPINFADELOADER)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.ballspinfadeloader_avloading, null), DYNAMIC_BOX_AV_BALLSPINFADELOADER);
            }
            if (tagList.contains(DYNAMIC_BOX_AV_LINESCALEPARTY)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.linescaleparty_avloading, null), DYNAMIC_BOX_AV_LINESCALEPARTY);
            }
            if (tagList.contains(DYNAMIC_BOX_AV_PACMAN)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.pacman_avloading, null), DYNAMIC_BOX_AV_PACMAN);
            }

            if (tagList.contains(DYNAMIC_BOX_MK_SHARINGAN)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.sharingan_mkloader, null), DYNAMIC_BOX_MK_SHARINGAN);
            }
            if (tagList.contains(DYNAMIC_BOX_MK_TWINFISHESSPINNER)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.twinfishesspinner_mkloader, null), DYNAMIC_BOX_MK_TWINFISHESSPINNER);
            }
            if (tagList.contains(DYNAMIC_BOX_MK_CLASSICSPINNER)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.classicspinner_mkloader, null), DYNAMIC_BOX_MK_CLASSICSPINNER);
            }
            if (tagList.contains(DYNAMIC_BOX_MK_LINESPINNER)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.linespinner_mkloader, null), DYNAMIC_BOX_MK_LINESPINNER);
            }
            if (tagList.contains(DYNAMIC_BOX_MK_FISHSPINNER)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.fishspinner_mkloader, null), DYNAMIC_BOX_MK_FISHSPINNER);
            }
            if (tagList.contains(DYNAMIC_BOX_MK_PHONEWAVE)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.phonewave_mkloader, null), DYNAMIC_BOX_MK_PHONEWAVE);
            }
            if (tagList.contains(DYNAMIC_BOX_MK_THREEPULSE)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.threepulse_mkloader, null), DYNAMIC_BOX_MK_THREEPULSE);
            }
            if (tagList.contains(DYNAMIC_BOX_MK_FOURPULSE)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.fourpulse_mkloader, null), DYNAMIC_BOX_MK_FOURPULSE);
            }
            if (tagList.contains(DYNAMIC_BOX_MK_FIVEPULSE)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.fivepulse_mkloader, null), DYNAMIC_BOX_MK_FIVEPULSE);
            }
            if (tagList.contains(DYNAMIC_BOX_MK_WORM)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.worm_mkloader, null), DYNAMIC_BOX_MK_WORM);
            }
            if (tagList.contains(DYNAMIC_BOX_MK_WHIRLPOOL)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.whirlpool_mkloader, null), DYNAMIC_BOX_MK_WHIRLPOOL);
            }
            if (tagList.contains(DYNAMIC_BOX_MK_RADAR)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.radar_mkloader, null), DYNAMIC_BOX_MK_RADAR);
            }

            if (tagList.contains(DYNAMIC_BOX_LT_PRELOADER)) {
                dynamicBox.addCustomView(LayoutInflater.from(this).inflate(R.layout.preloader_lottie, null), DYNAMIC_BOX_LT_PRELOADER);
            }
        }

        return dynamicBox;
    }

    protected DynamicBox createDynamicBox() {
        return createDynamicBox(findViewById(android.R.id.content));
    }

    @Deprecated
    public static void showDynamicBoxLoadingLayout() {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_LOADING_LAYOUT));
    }

    public static void showDynamicBoxLoadingLayout(Context context) {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_LOADING_LAYOUT, context));
    }

    public static void showDynamicBoxLoadingLayout(DynamicBox dynamicBox, Context context) {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_LOADING_LAYOUT, new Object[]{dynamicBox}, context));
    }

    public static void showDynamicBoxLoadingLayout(DynamicBox[] dynamicBoxes, Context context) {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_LOADING_LAYOUT, dynamicBoxes, context));
    }

    @Deprecated
    public static void showDynamicBoxInternetOffLayout() {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_INTERNET_OFF_LAYOUT));
    }

    public static void showDynamicBoxInternetOffLayout(Context context) {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_INTERNET_OFF_LAYOUT, context));
    }

    public static void showDynamicBoxInternetOffLayout(DynamicBox dynamicBox, Context context) {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_INTERNET_OFF_LAYOUT, new Object[]{dynamicBox}, context));
    }

    public static void showDynamicBoxInternetOffLayout(DynamicBox[] dynamicBoxes, Context context) {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_INTERNET_OFF_LAYOUT, dynamicBoxes, context));
    }

    @Deprecated
    public static void showDynamicBoxExceptionLayout() {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_EXCEPTION_LAYOUT));
    }

    public static void showDynamicBoxExceptionLayout(Context context) {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_EXCEPTION_LAYOUT, context));
    }

    public static void showDynamicBoxExceptionLayout(DynamicBox dynamicBox, Context context) {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_EXCEPTION_LAYOUT, new Object[]{dynamicBox}, context));
    }

    public static void showDynamicBoxExceptionLayout(DynamicBox[] dynamicBoxes, Context context) {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_EXCEPTION_LAYOUT, dynamicBoxes, context));
    }

    @Deprecated
    public static void showDynamicBoxCustomView(String tag) {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_CUSTOM_VIEW, new Object[]{tag}));
    }

    public static void showDynamicBoxCustomView(String tag, Context context) {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_CUSTOM_VIEW, new Object[]{tag}, context));
    }

    public static void showDynamicBoxCustomView(DynamicBox dynamicBox, String tag, Context context) {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_CUSTOM_VIEW, new Object[]{tag, dynamicBox}, context));
    }

    public static void showDynamicBoxCustomView(String tag, DynamicBox[] dynamicBoxes, Context context) {
        List args = new ArrayList();
        args.add(tag);
        args.addAll(Arrays.asList(dynamicBoxes));
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_CUSTOM_VIEW, args.toArray(), context));
    }

    @Deprecated
    public static void dismissDynamicBox() {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_HIDE_ALL));
    }

    public static void dismissDynamicBox(Context context) {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_HIDE_ALL, context));
    }

    public static void dismissDynamicBox(DynamicBox dynamicBox, Context context) {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_HIDE_ALL, new Object[]{dynamicBox}, context));
    }

    public static void dismissDynamicBox(DynamicBox[] dynamicBoxes, Context context) {
        RxBus.getInstance().post(DynamicBoxEvent.class, new DynamicBoxEvent(DynamicBoxEvent.TYPE_HIDE_ALL, dynamicBoxes, context));
    }

    public void setPendingTransition(String pendingTransition) {
        this.pendingTransition = pendingTransition;
    }

    private static class DynamicBoxEvent {
        final static int TYPE_LOADING_LAYOUT = 0;
        final static int TYPE_INTERNET_OFF_LAYOUT = 1;
        final static int TYPE_EXCEPTION_LAYOUT = 2;
        final static int TYPE_CUSTOM_VIEW = 3;
        final static int TYPE_HIDE_ALL = 4;

        int type;
        Object[] args;
        Context context;

        DynamicBoxEvent(int type) {
            this.type = type;
        }

        DynamicBoxEvent(int type, Object[] args) {
            this.type = type;
            this.args = args;
        }

        DynamicBoxEvent(int type, Context context) {
            this.type = type;
            this.context = context;
        }

        DynamicBoxEvent(int type, Object[] args, Context context) {
            this.type = type;
            this.args = args;
            this.context = context;
        }
    }
}
