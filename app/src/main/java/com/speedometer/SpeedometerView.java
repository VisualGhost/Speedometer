package com.speedometer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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

    private Paint mArchPaint1;
    private Paint mArchPaint2;
    private Paint mArchPaint3;
    private Paint mArchPaint4;

    private Paint mCenterCircleShadowPaint;
    private Paint mCenterCirclePaint;
    private Paint mNeedlePaint;

    private float mWidth;
    private float mHeight;
    private int[] mColors;
    private float mNeedleAngle = 90;
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
        initArchPaint1();
        initArchPaint2();
        initArchPaint3();
        initArchPaint4();
        initCenterCircleShadowPaint();
        initCenterCirclePaint();
        initNeedlePaint();
    }

    private void initWidgetSize() {
        mWidth = 2 * (mArchWidth + mArchRadius + mCenterCircleRadius);
        mHeight = (float) (2 * mArchWidth + mArchRadius + mCenterCircleRadius + Math.sin(mAngle * Math.PI / 180)
                * (mArchRadius + mCenterCircleRadius));
    }

    private void initArchPaint1() {
        mArchPaint1 = new Paint();
        mArchPaint1.setStyle(Paint.Style.FILL);
        mArchPaint1.setStrokeWidth(mArchWidth);
        mArchPaint1.setAntiAlias(true);
        mArchPaint1.setColor(Color.argb(255, 158, 180, 216));
        //Shader shader = new SweepGradient(mWidth / 2, 1.1f * mHeight, mColors, new float[]{0.6f, 0.7f, 0.8f, 0.9f});
        // mArchPaint.setShader(shader);
    }

    private void initArchPaint2() {
        mArchPaint2 = new Paint();
        mArchPaint2.setStyle(Paint.Style.FILL);
        mArchPaint2.setStrokeWidth(mArchWidth);
        mArchPaint2.setAntiAlias(true);
        mArchPaint2.setColor(Color.argb(255, 158, 198, 216));
        //Shader shader = new SweepGradient(mWidth / 2, 1.1f * mHeight, mColors, new float[]{0.6f, 0.7f, 0.8f, 0.9f});
        // mArchPaint.setShader(shader);
    }

    private void initArchPaint3() {
        mArchPaint3 = new Paint();
        mArchPaint3.setStyle(Paint.Style.FILL);
        mArchPaint3.setStrokeWidth(mArchWidth);
        mArchPaint3.setAntiAlias(true);
        mArchPaint3.setColor(Color.argb(255, 182, 216, 158));
        //Shader shader = new SweepGradient(mWidth / 2, 1.1f * mHeight, mColors, new float[]{0.6f, 0.7f, 0.8f, 0.9f});
        // mArchPaint.setShader(shader);
    }

    private void initArchPaint4() {
        mArchPaint4 = new Paint();
        mArchPaint4.setStyle(Paint.Style.FILL);
        mArchPaint4.setStrokeWidth(mArchWidth);
        mArchPaint4.setAntiAlias(true);
        mArchPaint4.setColor(Color.WHITE);
        mArchPaint4.setAlpha(100);
        //Shader shader = new SweepGradient(mWidth / 2, 1.1f * mHeight, mColors, new float[]{0.6f, 0.7f, 0.8f, 0.9f});
        // mArchPaint.setShader(shader);
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
        mCenterCirclePaint.setStyle(Paint.Style.FILL);
        mCenterCirclePaint.setAntiAlias(true);
        mCenterCirclePaint.setStrokeWidth(mCenterCircleStroke);
        mCenterCirclePaint.setColor(Color.WHITE);
    }

    private void initNeedlePaint() {
        mNeedlePaint = new Paint();
        mNeedlePaint.setStyle(Paint.Style.STROKE);
        mNeedlePaint.setAntiAlias(true);
        mNeedlePaint.setStrokeWidth(mNeedleWidth);
    }

    RectF rectF;
    float d = 100;
    RectF rectF2;


    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        setMeasuredDimension((int) mWidth, (int) mHeight);
        rectF = new RectF(mArchWidth, mArchWidth, mWidth - mArchWidth, mWidth - mArchWidth);
        rectF2 = new RectF(mArchWidth + d, mArchWidth + d, mWidth - mArchWidth - d, mWidth - mArchWidth - d);

    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        drawArch1(canvas, mArchWidth);

        if (mIs3) drawArch3(canvas, mArchWidth);
        if (mIs2) drawArch2(canvas, mArchWidth);
        drawArch4(canvas, mArchWidth);
        drawCenterCircle(canvas, mCenterCirclePaint);
    }

    private void drawArch1(Canvas canvas, float archWidth) {
        float startAngle = 180 - mAngle;
        canvas.drawArc(rectF, startAngle, Math.min(mAnimated, mMax1), true, mArchPaint1);
    }

    private void drawArch2(Canvas canvas, float archWidth) {
        float startAngle = 180 - mAngle;
        canvas.drawArc(rectF, startAngle + Math.min(mAnimated, mMax1), mAnimated - mMax1, true, mArchPaint2);
    }

    private float foo;

    private void drawArch3(Canvas canvas, float archWidth) {
        float startAngle = 180 - mAngle;
        canvas.drawArc(rectF, startAngle + mMax2, foo, true, mArchPaint3);
    }

    private void drawArch4(Canvas canvas, float archWidth) {
        float startAngle = 180 - mAngle;
        canvas.drawArc(rectF2, startAngle, startAngle + 180 - mAngle, true, mArchPaint4);
    }

    private float radius;

    private void drawCenterCircle(Canvas canvas, Paint paint) {
        float left = getWidth() / 2 - mCenterCircleRadius;
        float right = getWidth() / 2 + mCenterCircleRadius;
        RectF rectF = new RectF(left - radius, left - radius, right + radius, right + radius);
        canvas.drawArc(rectF, 0, 360, true, paint);
    }

    private float mAnimated;
    private boolean mIs2;

    public void animateMe(float v) {
        mAnimated = v;
        mIs2 = Float.compare(v, mMax1) > 0;
        foo = mMax3 - mMax2;
        invalidate();
    }

    private float mMax1;
    private float mMax2;
    private float mMax3;
    private boolean mIs3;

    public void setMax3(float max1, float max2, float max3) {
        mMax1 = max1;
        mMax2 = max2;
        mMax3 = max3;
    }

    public void reset() {
        mIs2 = false;
        mIs3 = false;
        mCenterCirclePaint.setColor(Color.WHITE);
        radius = 0;
    }

    public void show3() {
        mIs3 = true;
    }

    public void sector3(float v, int progress) {
        if (!mIs3) {
            return;
        }
        foo = v;
        radius = 300 * (1 - (progress / 100f));
        if (progress == 100) {
            mCenterCirclePaint.setColor(Color.WHITE);
        } else {
            mCenterCirclePaint.setColor(Color.argb(255, 244, 140, 66));
        }
        invalidate();
    }

    public void setCircleProgress(float progress) {
        if (!mIs3) {
            return;
        }
        radius = 300 * (1 - (progress / 100f));
        if (progress == 100) {
            mCenterCirclePaint.setColor(Color.WHITE);
        } else {
            mCenterCirclePaint.setColor(Color.argb(255, 244, 140, 66));
        }
        invalidate();
    }
}
