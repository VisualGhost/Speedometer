package com.speedometer.surfaceview;


import android.graphics.Canvas;

public interface ViewController {

    void onSize(int w, int h);

    void draw(Canvas canvas, float sweepAngle);

    void slide(Canvas canvas, float sweepAngle, int progress);

    void reset();
}
