package com.github.gnastnosaj.boilerplate.sample;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;

import com.github.gnastnosaj.boilerplate.conceal.Conceal;
import com.github.gnastnosaj.boilerplate.ui.activity.BaseActivity;
import com.github.gnastnosaj.boilerplate.animation.ui.widget.PageFlipView;

/**
 * Created by jasontsang on 4/21/17.
 */

public class MainActivity extends BaseActivity {

    PageFlipView mPageFlipView;
    ViewFlipper mViewFlipper;

    GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        mPageFlipView = new PageFlipView(this, mViewFlipper);
        frameLayout.addView(mPageFlipView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                frameLayout.bringToFront();
                mPageFlipView.onFingerDown(e.getX(), e.getY());
                mPageFlipView.onFingerUp(e.getX(), e.getY());
                return true;
            }
        });

        Conceal.conceal(this, SampleService.class);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
}

