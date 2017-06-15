package com.speedometer.surfaceview;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.ResourceObserver;

public class PieChartImpl extends SurfaceView implements PieChart, SurfaceHolder.Callback2 {

    private ConnectableObservable<Float> mConnectableObservable;
    private ResourceObserver<Float> mResourceObserver;
    private ViewController mViewController;
    private Bitmap mBitmap;
    private RectF mRectF;

    public PieChartImpl(Context context, Bitmap bitmap) {
        super(context, null);
        mBitmap = bitmap;
        getHolder().addCallback(PieChartImpl.this);
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
        // empty
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas(null);
            synchronized (getHolder()) {
                if (canvas != null && mBitmap != null) {
                    canvas.drawBitmap(mBitmap, null, mRectF, null);
                }
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // empty
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        holder.removeCallback(this);
        if (mResourceObserver != null) {
            mResourceObserver.dispose();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int smallest = Math.min(w, h);
        mRectF = new RectF(0, 0, w, h);
        if (mViewController != null) {
            mViewController.onSize(smallest, smallest);
        }
    }

    @Override
    public void setViewController(ViewController viewController) {
        mViewController = viewController;
    }

    @Override
    public void setObservableHolder(ObservableHolder observableHolder) {
        mConnectableObservable = observableHolder.getObservable().replay();
    }

    @Override
    public void startAnimation() {
        reset();
        if (mResourceObserver != null) {
            mResourceObserver.dispose();
        }

        mResourceObserver = createFloatResourceObserver();

        if (mConnectableObservable != null) {
            mConnectableObservable.connect();
            mConnectableObservable.subscribe(mResourceObserver);
        }
    }

    @Override
    public void slide(float sweepAngle, int progress) {
        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas(null);
            if (canvas != null) {
                if (mViewController != null) {
                    mViewController.slide(canvas, sweepAngle, progress);
                }
            }
        } finally {
            if (canvas != null) {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    private ResourceObserver<Float> createFloatResourceObserver() {
        return new ResourceObserver<Float>() {
            @Override
            public void onNext(Float aFloat) {
                Canvas canvas = null;
                try {
                    canvas = getHolder().lockCanvas(null);
                    synchronized (getHolder()) {
                        if (canvas != null) {
                            canvas.drawColor(Color.WHITE);
                            if (mViewController != null) {
                                mViewController.draw(canvas, aFloat);
                            }
                        }
                    }
                } finally {
                    if (canvas != null) {
                        getHolder().unlockCanvasAndPost(canvas);
                    }
                }

            }

            @Override
            public void onError(Throwable e) {
                // empty
            }

            @Override
            public void onComplete() {
                // empty
            }
        };
    }

    private void reset() {
        mViewController.reset();
    }
}
