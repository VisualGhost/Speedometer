package com.speedometer.surfaceview;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ObservableHolder {

    private static final long DELAY = 300;
    private final Scheduler SCHEDULER = Schedulers.from(Executors.newSingleThreadExecutor());

    private Observable<Float> mObservable;

    public static class Range {
        float startArch;
        float endArch;
        long duration;

        public Range(float startArch, float endArch, long duration) {
            this.startArch = startArch;
            this.endArch = endArch;
            this.duration = duration;
        }
    }

    public ObservableHolder(Range clockwiseRange, Range counterclockwiseRange) {
        Observable<Float> clockwiseObservable = createObservable(clockwiseRange.duration,
                clockwiseRange.startArch, clockwiseRange.endArch);
        Observable<Float> counterclockwiseObservable = createObservable(
                counterclockwiseRange.duration, counterclockwiseRange.startArch,
                counterclockwiseRange.endArch);

        List<Observable<Float>> observables = new ArrayList<>();
        observables.add(clockwiseObservable);
        observables.add(counterclockwiseObservable);

        mObservable = Observable.<Observable<Float>>fromIterable(observables)
                .concatMap(new Function<Observable<Float>, ObservableSource<? extends Float>>() {
                    @Override
                    public ObservableSource<? extends Float> apply(@NonNull Observable<Float> floatObservable) throws Exception {
                        return floatObservable;
                    }
                });
    }

    public Observable<Float> getObservable() {
        return mObservable;
    }

    private Observable<Float> createObservable(final long duration, final float startArch, final float endArch) {
        return Observable.create(new ObservableOnSubscribe<Float>() {
            @Override
            public void subscribe(final ObservableEmitter<Float> e) throws Exception {
                animate(e, duration, startArch, endArch);
            }
        }).delay(DELAY, TimeUnit.MILLISECONDS).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(SCHEDULER);
    }

    private void animate(final ObservableEmitter<Float> e, long duration, float startArch, float endArch) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(startArch, endArch);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!e.isDisposed()) {
                    e.onNext((float) animation.getAnimatedValue());
                }
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                e.onComplete();
            }
        });
        valueAnimator.start();
    }
}
