package com.speedometer.surfaceview;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SurfaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new PieChart(this, new ViewController() {

            Paint mPaint = new Paint();

            {
                mPaint.setColor(Color.GREEN);
                mPaint.setTextSize(170);
            }

            @Override
            public void draw(Canvas canvas, float f) {
                canvas.drawText("ABC " + f, 100, 500, mPaint);
            }
        }));
    }
}
