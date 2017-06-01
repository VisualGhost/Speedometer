package com.speedometer.surfaceview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class ViewControllerImpl implements ViewController {

    private float mAngle = 45;
    private Paint mArchPaint1;
    private Paint mArchPaint2;
    private Paint mArchPaint3;
    private Paint mArchPaint4;
    private RectF rectF;
    private float mMax;
    private float mMax1;
    private float mMax2;
    private float mMax3;
    private float mProgress;

    public ViewControllerImpl(float max1, float max2, float max3, float max) {
        initArchPaint1();
        initArchPaint2();
        initArchPaint3();
        initArchPaint4();
        mMax1 = max1;
        mMax2 = max2;
        mMax3 = max3;
        mMax = max;
    }

    private void initArchPaint1() {
        mArchPaint1 = new Paint();
        mArchPaint1.setStyle(Paint.Style.FILL);
        mArchPaint1.setAntiAlias(true);
        mArchPaint1.setColor(Color.argb(255, 158, 180, 216));
    }

    private void initArchPaint2() {
        mArchPaint2 = new Paint();
        mArchPaint2.setStyle(Paint.Style.FILL);
        mArchPaint2.setAntiAlias(true);
        mArchPaint2.setColor(Color.argb(255, 158, 198, 216));
    }

    private void initArchPaint3() {
        mArchPaint3 = new Paint();
        mArchPaint3.setStyle(Paint.Style.FILL);
        mArchPaint3.setAntiAlias(true);
        mArchPaint3.setColor(Color.argb(255, 182, 216, 158));
    }

    private void initArchPaint4() {
        mArchPaint4 = new Paint();
        mArchPaint4.setStyle(Paint.Style.FILL);
        mArchPaint4.setAntiAlias(true);
        mArchPaint4.setColor(Color.WHITE);
        mArchPaint4.setAlpha(100);
    }

    @Override
    public void onSize(int w, int h) {
        rectF = new RectF(0, 0, w, h);
    }

    @Override
    public void draw(Canvas canvas, float f) {
        drawArch1(canvas, f);
        if (mProgress > 0) {
            drawArch3(canvas);
        }
        if (Float.compare(f, mMax1) > 0) {
            drawArch2(canvas, f);
        }
    }

    private void drawArch1(Canvas canvas, float f) {
        float startAngle = 180 - mAngle;
        canvas.drawArc(rectF, startAngle, Math.min(f, mMax1), true, mArchPaint1);
    }

    private void drawArch2(Canvas canvas, float f) {
        float startAngle = 180 - mAngle;
        canvas.drawArc(rectF, startAngle + Math.min(f, mMax1), f - mMax1, true, mArchPaint2);
    }

    private void drawArch3(Canvas canvas) {
        float startAngle = 180 - mAngle;
        canvas.drawArc(rectF, startAngle + mMax2, mProgress, true, mArchPaint3);
    }

    @Override
    public void reset() {
        // empty
    }
}
