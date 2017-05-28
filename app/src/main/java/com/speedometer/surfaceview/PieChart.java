package com.speedometer.surfaceview;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

public class PieChart extends SurfaceView implements SurfaceHolder.Callback2 {

    private Observable<Float> observable;
    private ResourceObserver<Float> mResourceObserver;
    private ViewController mViewController;

    public PieChart(Context context, ViewController viewController) {
        super(context, null);
        mViewController = viewController;
        getHolder().addCallback(this);
        Scheduler scheduler = Schedulers.from(Executors.newSingleThreadExecutor());
        observable = Observable.create(new ObservableOnSubscribe<Float>() {
            @Override
            public void subscribe(final ObservableEmitter<Float> e) throws Exception {
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 270);
                valueAnimator.setDuration(3000);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        e.onNext((float) animation.getAnimatedValue());
                    }
                });
                valueAnimator.start();
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(scheduler);


    }

    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {

    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        Log.d("Test", "surfaceCreated: " + Thread.currentThread().getName());
        mResourceObserver = new ResourceObserver<Float>() {
            @Override
            public void onNext(Float aFloat) {
                Log.d("Test", "Float: " + aFloat);
                Canvas canvas = null;
                try {
                    canvas = holder.lockCanvas(null);
                    synchronized (holder) {
                        canvas.drawColor(Color.BLACK);
                        mViewController.draw(canvas, aFloat);
                    }
                } finally {
                    if (canvas != null) {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }

            }

            @Override
            public void onError(Throwable e) {
                Log.d("Test", e.toString());
            }

            @Override
            public void onComplete() {

            }
        };
        observable.subscribe(mResourceObserver);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        holder.removeCallback(this);
        mResourceObserver.dispose();

    }
}
