package com.speedometer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

public class SpeedometerView extends View {

    private int mMinGreenColor;
    private int mMaxGreenColor;
    private int mMinRedColor;
    private int mMaxRedColor;
    private int mCenterCircleColor;
    private float mNeedleWidth;
    private float mNeedleLength;
    private float mArchWidth;
    private float mArchRadius;
    private float mAngle;
    private float mCenterCircleRadius;
    private float mCenterCircleStroke;
    private float mShadowShift;
    private int mShadowColor;
    private float mShadowRadius;

    private Paint mArchPaint;
    private Paint mCenterCircleShadowPaint;
    private Paint mCenterCirclePaint;
    private Paint mNeedlePaint;

    private float mWidth;
    private float mHeight;
    private int[] mColors;
    private float mNeedleAngle;
    private float mSweepAngle;

    public SpeedometerView(final Context context) {
        super(context);
        init(context, null);
    }

    public SpeedometerView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SpeedometerView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        if (attrs != null) {
            TypedArray array = null;
            try {
                array = context.obtainStyledAttributes(attrs, R.styleable.SpeedometerView);
                mMinGreenColor = array.getColor(R.styleable.SpeedometerView_minGreen, Color.WHITE);
                mMaxGreenColor = array.getColor(R.styleable.SpeedometerView_maxGreen, Color.WHITE);
                mMinRedColor = array.getColor(R.styleable.SpeedometerView_minRed, Color.WHITE);
                mMaxRedColor = array.getColor(R.styleable.SpeedometerView_maxRed, Color.WHITE);
                float scale = array.getFloat(R.styleable.SpeedometerView_scale, 1f);
                mNeedleWidth = scale * array.getDimension(R.styleable.SpeedometerView_needleWidth, 0f);
                mNeedleLength = scale * array.getDimension(R.styleable.SpeedometerView_needleLength, 0f);
                mArchWidth = scale * array.getDimension(R.styleable.SpeedometerView_archWidth, 0f);
                mArchRadius = scale * array.getDimension(R.styleable.SpeedometerView_archRadius, 0f);
                mAngle = array.getFloat(R.styleable.SpeedometerView_angle, 0f);
                mCenterCircleRadius = scale * array.getDimension(R.styleable.SpeedometerView_centerCircleRadius, 0f);
                mCenterCircleStroke = scale * array.getDimension(R.styleable.SpeedometerView_centerCircleStroke, 0f);
                mCenterCircleColor = array.getColor(R.styleable.SpeedometerView_centerCircleColor, Color.WHITE);
                mShadowShift = array.getDimension(R.styleable.SpeedometerView_shadowShift, 0f);
                mShadowColor = array.getColor(R.styleable.SpeedometerView_shadowColor, Color.WHITE);
                mShadowRadius = array.getFloat(R.styleable.SpeedometerView_shadowRadius, 0f);
            } finally {
                if (array != null) {
                    array.recycle();
                }
            }
        }
        mColors = new int[]{mMaxGreenColor, mMinGreenColor, mMinRedColor, mMaxRedColor};
        mSweepAngle = 180 + 2 * mAngle;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        initWidgetSize();
        initArchPaint();
        initCenterCircleShadowPaint();
        initCenterCirclePaint();
        initNeedlePaint();
    }

    private void initWidgetSize() {
        mWidth = 2 * (mArchWidth + mArchRadius + mCenterCircleRadius);
        mHeight = (float) (2 * mArchWidth + mArchRadius + mCenterCircleRadius + Math.sin(mAngle * Math.PI / 180) * (mArchRadius + mCenterCircleRadius));
    }

    private void initArchPaint() {
        mArchPaint = new Paint();
        mArchPaint.setStyle(Paint.Style.STROKE);
        mArchPaint.setStrokeWidth(mArchWidth);
        mArchPaint.setAntiAlias(true);
        Shader shader = new SweepGradient(mWidth / 2, 1.1f * mHeight, mColors, new float[]{0.6f, 0.7f, 0.8f, 0.9f});
        mArchPaint.setShader(shader);
    }

    private void initCenterCircleShadowPaint() {
        mCenterCircleShadowPaint = new Paint();
        mCenterCircleShadowPaint.setStyle(Paint.Style.STROKE);
        mCenterCircleShadowPaint.setAntiAlias(true);
        mCenterCircleShadowPaint.setStrokeWidth(mCenterCircleStroke);
        mCenterCircleShadowPaint.setColor(mShadowColor);
        mCenterCircleShadowPaint.setMaskFilter(new BlurMaskFilter(mShadowRadius, BlurMaskFilter.Blur.NORMAL));
    }

    private void initCenterCirclePaint() {
        mCenterCirclePaint = new Paint();
        mCenterCirclePaint.setStyle(Paint.Style.STROKE);
        mCenterCirclePaint.setAntiAlias(true);
        mCenterCirclePaint.setStrokeWidth(mCenterCircleStroke);
        mCenterCirclePaint.setColor(mCenterCircleColor);
    }

    private void initNeedlePaint() {
        mNeedlePaint = new Paint();
        mNeedlePaint.setStyle(Paint.Style.STROKE);
        mNeedlePaint.setAntiAlias(true);
        mNeedlePaint.setStrokeWidth(mNeedleWidth);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        setMeasuredDimension((int) mWidth, (int) mHeight);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        drawArch(canvas, mArchWidth);
        drawCenterCircle(canvas, mCenterCircleShadowPaint, mShadowShift);
        drawNeedle(canvas, -mNeedleAngle, mCenterCircleShadowPaint, mShadowShift);
        drawNeedle(canvas, -mNeedleAngle, mNeedlePaint, 0);
        drawCenterCircle(canvas, mCenterCirclePaint, 0);
        mNeedlePaint.setColor(getNeedleColor(mNeedleAngle));
    }

    private void drawArch(Canvas canvas, float archWidth) {
        RectF rectF = new RectF(archWidth, archWidth, getWidth() - archWidth, getWidth() - archWidth);
        float startAngle = 180 - mAngle;
        canvas.drawArc(rectF, startAngle, mSweepAngle, false, mArchPaint);
    }

    private void drawCenterCircle(Canvas canvas, Paint paint, float yShift) {
        float left = getWidth() / 2 - mCenterCircleRadius;
        float right = getWidth() / 2 + mCenterCircleRadius;
        RectF rectF = new RectF(left, left + yShift, right, right + yShift);
        canvas.drawArc(rectF, 0, 360, false, paint);
    }

    private void drawNeedle(Canvas canvas, float angle, Paint paint, float yShift) {
        float centerX = getWidth() / 2;
        float centerY = getWidth() / 2;
        float c = (float) Math.cos(angle * Math.PI / 180);
        float s = (float) Math.sin(angle * Math.PI / 180);
        canvas.drawLine(centerX + c * mCenterCircleRadius, centerY + s * mCenterCircleRadius + yShift, centerX + c * mNeedleLength, centerY + s * mNeedleLength + yShift, paint);
    }

    private int getAverageColor(int color1, int color2, float percent) {
        int a = ave(Color.alpha(color1), Color.alpha(color2), percent);
        int r = ave(Color.red(color1), Color.red(color2), percent);
        int g = ave(Color.green(color1), Color.green(color2), percent);
        int b = ave(Color.blue(color1), Color.blue(color2), percent);

        return Color.argb(a, r, g, b);
    }

    private int ave(int s, int d, float p) {
        return Math.round(s + (1 - p) * (d - s));
    }

    private int getNeedleColor(float angle) {
        if (angle >= -mAngle && angle <= mSweepAngle) {
            if (angle <= 63) {
                return getAverageColor(mColors[2], mColors[3], (angle + mAngle) / (63 + mAngle));
            }
            if (angle > 63 && angle <= 117) {
                return getAverageColor(mColors[1], mColors[2], (angle - 63) / (117 - 63));
            }
            if (angle > 117) {
                return getAverageColor(mColors[0], mColors[1], (angle - 117) / (198 - 117));
            }
        }
        return 0;
    }

    public void setNeedleAngle(float needleAngle) {
        mNeedleAngle = needleAngle;
        invalidate();
    }
}
