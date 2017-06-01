package com.speedometer.surfaceview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.ResourceObserver;

public class PieChartImpl extends SurfaceView implements PieChart, SurfaceHolder.Callback2 {

    private ConnectableObservable<Float> mConnectableObservable;
    private ResourceObserver<Float> mResourceObserver;
    private ViewController mViewController;
    private SurfaceHolder mHolder;

    public PieChartImpl(Context context) {
        super(context, null);
        init();
    }

    public PieChartImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
        // empty
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        mHolder = holder;
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas(null);
            synchronized (holder) {
                if (canvas != null) {
                    canvas.drawColor(Color.WHITE);
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

        mResourceObserver = new ResourceObserver<Float>() {
            @Override
            public void onNext(Float aFloat) {
                Canvas canvas = null;
                try {
                    canvas = mHolder.lockCanvas(null);
                    synchronized (mHolder) {
                        if (canvas != null) {
                            canvas.drawColor(Color.WHITE);
                            if (mViewController != null) {
                                mViewController.draw(canvas, aFloat);
                            }
                        }
                    }
                } finally {
                    if (canvas != null) {
                        mHolder.unlockCanvasAndPost(canvas);
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

        if (mConnectableObservable != null) {
            mConnectableObservable.connect();
            mConnectableObservable.subscribe(mResourceObserver);
        }
    }

    private void reset() {
        mViewController.reset();
    }
}
