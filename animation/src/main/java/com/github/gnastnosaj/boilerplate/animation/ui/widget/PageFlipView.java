package com.github.gnastnosaj.boilerplate.animation.ui.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;
import android.os.Message;
import android.widget.ViewFlipper;

import com.eschao.android.widget.pageflip.PageFlip;
import com.eschao.android.widget.pageflip.PageFlipException;

import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import timber.log.Timber;

public class PageFlipView extends GLSurfaceView implements Renderer {

    private Handler mHandler;
    private PageFlip mPageFlip;
    private PageRender mPageRender;
    private ReentrantLock mDrawLock;

    private int mDuration = 2000;

    public PageFlipView(Context context, ViewFlipper viewFlipper) {
        super(context);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case PageRender.MSG_ENDED_DRAWING_FRAME:
                        try {
                            mDrawLock.lock();

                            if (mPageRender != null && mPageRender.onEndedDrawing(msg.arg1)) {
                                requestRender();
                            }
                        } finally {
                            mDrawLock.unlock();
                        }
                        break;

                    default:
                        break;
                }
            }
        };

        mPageFlip = new PageFlip(context);
        mPageFlip.setShadowWidthOfFoldEdges(5, 60, 0.3f)
                .setShadowWidthOfFoldBase(5, 80, 0.4f)
                .enableAutoPage(false);

        setEGLContextClientVersion(2);

        mDrawLock = new ReentrantLock();
        mPageRender = new PageRender(mPageFlip, mHandler, viewFlipper);

        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public int getAnimateDuration() {
        return mDuration;
    }

    public void setAnimateDuration(int duration) {
        mDuration = duration;
    }

    public void onFingerDown(float x, float y) {
        if (!mPageFlip.isAnimating() && mPageFlip.getFirstPage() != null) {
            mPageFlip.onFingerDown(x, y);
        }
    }

    public void onFingerMove(float x, float y) {
        if (mPageFlip.isAnimating()) {
        } else if (mPageFlip.canAnimate(x, y)) {
            onFingerUp(x, y);
        } else if (mPageFlip.onFingerMove(x, y)) {
            try {
                mDrawLock.lock();
                if (mPageRender != null && mPageRender.onFingerMove(x, y)) {
                    requestRender();
                }
            } finally {
                mDrawLock.unlock();
            }
        }
    }

    public void onFingerUp(float x, float y) {
        if (!mPageFlip.isAnimating()) {
            mPageFlip.onFingerUp(x, y, mDuration);
            try {
                mDrawLock.lock();
                if (mPageRender != null && mPageRender.onFingerUp(x, y)) {
                    requestRender();
                }
            } finally {
                mDrawLock.unlock();
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        try {
            mDrawLock.lock();
            if (mPageRender != null) {
                mPageRender.onDrawFrame();
            }
        } finally {
            mDrawLock.unlock();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        try {
            mPageFlip.onSurfaceChanged(width, height);

            mPageRender.onSurfaceChanged(width, height);
        } catch (PageFlipException e) {
            Timber.e(e);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        try {
            mPageFlip.onSurfaceCreated();
        } catch (PageFlipException e) {
            Timber.e(e);
        }
    }
}
