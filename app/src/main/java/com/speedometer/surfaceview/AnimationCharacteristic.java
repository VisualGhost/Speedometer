package com.speedometer.surfaceview;


import android.util.Log;

public class AnimationCharacteristic {
    private boolean mIsSegment1Animated;
    private float mArch;

    public void setSegment1Animated(boolean segment1Animated) {
        mIsSegment1Animated = segment1Animated;
    }

    public void setArch(float arch) {
        mArch = arch;
    }

    public boolean isSegment1Animated() {
        return mIsSegment1Animated;
    }

    public float getArch() {
        return mArch;
    }
}
