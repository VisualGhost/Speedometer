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
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class SpeedometerView extends View {

    private static final float MIN_DEF_VALUE = 0;
    private static final float MAX_DEF_VALUE = 10;

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
    private float mMinValue;
    private float mMaxValue;

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
                mMinValue = array.getFloat(R.styleable.SpeedometerView_minValue, MIN_DEF_VALUE);
                mMaxValue = array.getFloat(R.styleable.SpeedometerView_maxValue, MAX_DEF_VALUE);
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
        mNeedlePaint.setColor(getNeedleColor(mNeedleAngle));
        drawNeedle(canvas, -mNeedleAngle, mCenterCircleShadowPaint, mShadowShift);
        drawNeedle(canvas, -mNeedleAngle, mNeedlePaint, 0);
        drawCenterCircle(canvas, mCenterCirclePaint, 0);
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
            float sectorAngleRange = mSweepAngle / mColors.length;
            float angle1 = 3 * sectorAngleRange / 2 - mAngle;
            if (angle <= angle1) {
                return getAverageColor(mColors[2], mColors[3], (angle + mAngle) / (angle1 + mAngle));
            }
            float angle2 = 90 + sectorAngleRange / 2;
            if (angle > angle1 && angle <= angle2) {
                return getAverageColor(mColors[1], mColors[2], (angle - angle1) / (angle2 - angle1));
            }
            if (angle > angle2) {
                return getAverageColor(mColors[0], mColors[1], (angle - angle2) / (mSweepAngle - mAngle - angle2));
            }
        }
        return 0;
    }

    /**
     * Sets the angle of needle in the range of [-angle, 180 + 2*angle] (e.g angle = 18, so [-18, 216]).
     * The "angle" - is an angle between horizontal line and the needle at its border (max and min) values.
     *
     * @param needleAngle The needle's angle.
     */
    private void setNeedleAngle(float needleAngle) {
        mNeedleAngle = needleAngle;
        invalidate();
    }

    /**
     * Converts from angle to value from [minValue, maxValue].
     *
     * @param angle The angle of needle.
     */
    private float convertToValue(float angle) {
        float maxAngle = mSweepAngle - mAngle;
        float minAngle = -mAngle;
        return ((mMaxValue - mMinValue) / (minAngle - maxAngle)) * (angle - maxAngle) + mMinValue;
    }

    /**
     * Converts value from [mMinValue, mMaxValue] to angle of needle.
     */
    private float convertToAngle(float value) {
        float maxAngle = mSweepAngle - mAngle;
        float minAngle = -mAngle;
        return maxAngle + (minAngle - maxAngle) / (mMaxValue - mMinValue) * (value - mMinValue);
    }

    public void setCurrentValue(float value) {
        if (value < mMinValue || value > mMaxValue) {
            throw new IllegalArgumentException("Wrong input value. Out of range: " + value + " does not belong to [" + mMinValue + ", " + mMaxValue + "]");
        }
        float angle = convertToAngle(value);
        setNeedleAngle(angle);
    }

    static class SavedState extends BaseSavedState {
        float angle;

        SavedState(Parcelable superState) {
            super(superState);
        }

        protected SavedState(Parcel in) {
            super(in);
            angle = (float) in.readSerializable();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeSerializable(angle);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        Parcelable superState = super.onSaveInstanceState();

        SavedState state = new SavedState(superState);
        state.angle = mNeedleAngle;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        mNeedleAngle = savedState.angle;
        invalidate();
    }
}
