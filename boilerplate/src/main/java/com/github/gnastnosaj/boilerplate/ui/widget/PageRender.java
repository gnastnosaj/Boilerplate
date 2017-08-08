package com.github.gnastnosaj.boilerplate.ui.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.widget.ViewFlipper;

import com.eschao.android.widget.pageflip.OnPageFlipListener;
import com.eschao.android.widget.pageflip.Page;
import com.eschao.android.widget.pageflip.PageFlip;
import com.eschao.android.widget.pageflip.PageFlipState;

import java.util.concurrent.CountDownLatch;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class PageRender implements OnPageFlipListener {

    protected final static int MSG_ENDED_DRAWING_FRAME = 1;

    protected final static int DRAW_MOVING_FRAME = 0;
    protected final static int DRAW_ANIMATING_FRAME = 1;
    protected final static int DRAW_FULL_PAGE = 2;

    private int mDrawCommand;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Handler mHandler;
    private PageFlip mPageFlip;
    private ViewFlipper mViewFlipper;

    public PageRender(PageFlip pageFlip, Handler handler, ViewFlipper viewFlipper) {
        mPageFlip = pageFlip;
        mDrawCommand = DRAW_FULL_PAGE;
        mCanvas = new Canvas();
        mPageFlip.setListener(this);
        mHandler = handler;
        mViewFlipper = viewFlipper;
    }

    public boolean onFingerMove(float x, float y) {
        mDrawCommand = DRAW_MOVING_FRAME;
        return true;
    }

    public boolean onFingerUp(float x, float y) {
        if (mPageFlip.animating()) {
            mDrawCommand = DRAW_ANIMATING_FRAME;
            return true;
        }

        return false;
    }

    public void onDrawFrame() {

        mPageFlip.deleteUnusedTextures();
        Page page = mPageFlip.getFirstPage();

        if (mDrawCommand == DRAW_MOVING_FRAME || mDrawCommand == DRAW_ANIMATING_FRAME) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            if (mPageFlip.getFlipState() == PageFlipState.FORWARD_FLIP) {
                if (!page.isSecondTextureSet()) {
                    Observable.just(mViewFlipper).observeOn(AndroidSchedulers.mainThread()).subscribe(viewFlipper -> {
                        viewFlipper.showNext();
                        countDownLatch.countDown();
                    });
                    try {
                        countDownLatch.await();
                    } catch (Exception e) {
                    }
                    drawPage();
                    page.setSecondTexture(mBitmap);
                }
            } else if (!page.isFirstTextureSet()) {
                Observable.just(mViewFlipper).observeOn(AndroidSchedulers.mainThread()).subscribe(viewFlipper -> {
                    viewFlipper.showPrevious();
                    countDownLatch.countDown();
                });
                try {
                    countDownLatch.await();
                } catch (Exception e) {
                }
                drawPage();
                page.setFirstTexture(mBitmap);
            }
            mPageFlip.drawFlipFrame();
        } else if (mDrawCommand == DRAW_FULL_PAGE) {
            if (!page.isFirstTextureSet()) {
                drawPage();
                page.setFirstTexture(mBitmap);
            }

            mPageFlip.drawPageFrame();
        }

        Message msg = Message.obtain();
        msg.what = MSG_ENDED_DRAWING_FRAME;
        msg.arg1 = mDrawCommand;
        mHandler.sendMessage(msg);
    }

    public void onSurfaceChanged(int width, int height) {
        if (mBitmap != null) {
            mBitmap.recycle();
        }

        Page page = mPageFlip.getFirstPage();
        mBitmap = Bitmap.createBitmap((int) page.width(), (int) page.height(), Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);
    }

    public boolean onEndedDrawing(int what) {
        if (what == DRAW_ANIMATING_FRAME) {
            boolean isAnimating = mPageFlip.animating();

            if (isAnimating) {
                mDrawCommand = DRAW_ANIMATING_FRAME;
                return true;
            } else {
                final PageFlipState state = mPageFlip.getFlipState();
                if (state == PageFlipState.END_WITH_BACKWARD) {
                    mViewFlipper.bringToFront();
                } else if (state == PageFlipState.END_WITH_FORWARD) {
                    mPageFlip.getFirstPage().setFirstTextureWithSecond();
                    mViewFlipper.bringToFront();
                }

                mDrawCommand = DRAW_FULL_PAGE;
                return true;
            }
        }
        return false;
    }

    public boolean canFlipForward() {
        return true;
    }

    public boolean canFlipBackward() {
        return false;
    }

    private void drawPage() {
        final int width = mCanvas.getWidth();
        final int height = mCanvas.getHeight();
        Paint p = new Paint();
        p.setFilterBitmap(true);

        Bitmap background = Bitmap.createBitmap(mViewFlipper.getWidth(), mViewFlipper.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(background);
        mViewFlipper.draw(canvas);
        Rect rect = new Rect(0, 0, width, height);
        mCanvas.drawBitmap(background, null, rect, p);
        background.recycle();
    }
}
