package com.speedometer.surfaceview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class ViewControllerImpl implements ViewController {

    private static final float START_ANGLE = 135;
    private static final float MIN_CIRCLE_FACTOR = 0.1f;
    private static final float MAX_CIRCLE_FACTOR = 0.3f;

    private Bitmap mBitmap;

    private Paint mArchPaintSegment1;
    private Paint mArchPaintSegment2;
    private Paint mArchPaintSegment3;
    private Paint mArchPaintSegment4;
    private Paint mCenterCirclePaint;

    private RectF rectF;

    private float mMaxSegmentAngle;
    private float mMaxSegment1Angle;
    private float mMaxSegment2Angle;
    private float mSweepAngle;
    private float mCurrentSweepAngle;
    private float radius;

    private int mWidth;
    private boolean isMaxReached;

    public ViewControllerImpl(Bitmap bitmap,
                              float maxSegment1Angle,
                              float maxSegment2Angle,
                              float maxSegmentAngle) {
        mBitmap = bitmap;
        initArchPaint1();
        initArchPaint2();
        initArchPaint3();
        initArchPaint4();
        initCenterCirclePaint();
        mMaxSegment1Angle = maxSegment1Angle;
        mMaxSegment2Angle = maxSegment2Angle;
        mMaxSegmentAngle = maxSegmentAngle;
    }

    private void initArchPaint1() {
        mArchPaintSegment1 = new Paint();
        mArchPaintSegment1.setStyle(Paint.Style.FILL);
        mArchPaintSegment1.setAntiAlias(true);
        //TODO use attr
        mArchPaintSegment1.setColor(Color.argb(255, 158, 180, 216));
    }

    private void initArchPaint2() {
        mArchPaintSegment2 = new Paint();
        mArchPaintSegment2.setStyle(Paint.Style.FILL);
        mArchPaintSegment2.setAntiAlias(true);
        //TODO use attr
        mArchPaintSegment2.setColor(Color.argb(255, 158, 198, 216));
    }

    private void initArchPaint3() {
        mArchPaintSegment3 = new Paint();
        mArchPaintSegment3.setStyle(Paint.Style.FILL);
        mArchPaintSegment3.setAntiAlias(true);
        //TODO use attr
        mArchPaintSegment3.setColor(Color.argb(255, 182, 216, 158));
    }

    private void initArchPaint4() {
        mArchPaintSegment4 = new Paint();
        mArchPaintSegment4.setStyle(Paint.Style.FILL);
        mArchPaintSegment4.setAntiAlias(true);
        //TODO use attr
        mArchPaintSegment4.setColor(Color.WHITE);
        mArchPaintSegment4.setAlpha(100);
    }

    private void initCenterCirclePaint() {
        mCenterCirclePaint = new Paint();
        mCenterCirclePaint.setStyle(Paint.Style.FILL);
        mCenterCirclePaint.setAntiAlias(true);
        mCenterCirclePaint.setColor(Color.WHITE);
    }

    @Override
    public void onSize(int w, int h) {
        rectF = new RectF(0, 0, w, h);
        mWidth = w;
        radius = MIN_CIRCLE_FACTOR * mWidth;
    }

    @Override
    public void draw(Canvas canvas, float sweepAngle) {
        mCurrentSweepAngle = sweepAngle;
        drawSegments(canvas, sweepAngle);
        float progress = getProgress(sweepAngle);
        drawCenterCircle(canvas, sweepAngle, progress);
    }

    private void drawSegments(Canvas canvas, float sweepAngle) {
        canvas.drawBitmap(mBitmap, null, rectF, null);
        drawSegment4(canvas);
        drawSegment1(canvas, sweepAngle);
        if (mSweepAngle > 0) {
            drawSegment3(canvas);
        }
        if (Float.compare(sweepAngle, mMaxSegment1Angle) > 0) {
            drawSegment2(canvas, sweepAngle);
        }
    }

    private float getProgress(float sweepAngle) {
        return 100 / (mMaxSegmentAngle - mMaxSegment2Angle) * (sweepAngle - mMaxSegment2Angle);
    }

    @Override
    public void slide(Canvas canvas, float sweepAngle, int progress) {
        mSweepAngle = sweepAngle;
        drawSegments(canvas, mCurrentSweepAngle);
        drawCenterCircle(canvas, sweepAngle, progress);
    }

    private void drawSegment1(Canvas canvas, float sweepAngle) {
        canvas.drawArc(rectF, START_ANGLE, Math.min(sweepAngle, mMaxSegment1Angle), true, mArchPaintSegment1);
    }

    private void drawSegment2(Canvas canvas, float sweepAngle) {
        canvas.drawArc(rectF, START_ANGLE + Math.min(sweepAngle, mMaxSegment1Angle), sweepAngle - mMaxSegment1Angle, true, mArchPaintSegment2);
    }

    private void drawSegment3(Canvas canvas) {
        canvas.drawArc(rectF, START_ANGLE + mMaxSegment2Angle, mSweepAngle, true, mArchPaintSegment3);
    }

    private void drawSegment4(Canvas canvas) {
        canvas.drawArc(rectF, START_ANGLE, 2 * START_ANGLE, true, mArchPaintSegment4);
    }

    private void modifyCircleRadius(float progress) {
        radius = mWidth * (MIN_CIRCLE_FACTOR + MAX_CIRCLE_FACTOR * (1 - (progress / 100f)));
    }

    private void modifyCircleColor(float progress) {
        if (progress == 100) {
            mCenterCirclePaint.setColor(Color.WHITE);// TODO use attr
        } else {
            mCenterCirclePaint.setColor(Color.argb(255, 244, 140, 66));// TODO use attr
        }
    }

    private void drawCenterCircle(Canvas canvas, float sweepAngle, float progress) {
        if (!isMaxReached && Float.compare(sweepAngle, mMaxSegmentAngle) == 0) {
            isMaxReached = true;
        }
        if (isMaxReached) {
            modifyCircleColor(progress);
            modifyCircleRadius(progress);
        }
        canvas.drawCircle(mWidth / 2, mWidth / 2, radius, mCenterCirclePaint);
    }

    @Override
    public void reset() {
        isMaxReached = false;
        radius = MIN_CIRCLE_FACTOR * mWidth;
        mCenterCirclePaint.setColor(Color.WHITE);// TODO use attr
    }
}
