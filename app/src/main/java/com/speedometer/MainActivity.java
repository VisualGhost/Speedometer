package com.speedometer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;


public class MainActivity extends ActionBarActivity {

    ValueAnimator mAnimator1;
    ValueAnimator mAnimator2;
    SpeedometerView speedometerView;
    SeekBar mSeekBar;

    private final static float MAX_1 = 110;
    private final static float MAX_2 = 190;
    private final static float MAX_3 = 240;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Money Saving Adjust");

        speedometerView = (SpeedometerView) findViewById(R.id.speedometer);
        speedometerView.setMax3(MAX_1, MAX_2, MAX_3);

        mAnimator1 = ValueAnimator.ofFloat(0, 270);// 60 grad = 1 sec
        mAnimator1.setDuration(calculateTime(0, 270));
        mAnimator1.setInterpolator(new LinearInterpolator());

        mAnimator2 = ValueAnimator.ofFloat(270, MAX_2);// 60 grad = 1 sec
        mAnimator2.setDuration(calculateTime(MAX_2, 270));
        mAnimator2.setInterpolator(new LinearInterpolator());

        mAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                speedometerView.animateMe(v);
            }
        });

        mAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                speedometerView.animateMe(v);
            }
        });

        mAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                speedometerView.show3();
                mAnimator2.start();
            }
        });

        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float f = (progress / 100f) * (270 - MAX_2);
                Log.d("Test", "Progress: " + f);
                speedometerView.sector3(f, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        setSeekBar();
    }

    private void setSeekBar(){
        float p = 100f * (MAX_3 - MAX_2) / (270f - MAX_2);
        mSeekBar.setProgress((int) p);
    }

    private long calculateTime(float start, float end) {
        return (long) Math.abs(start - end) * 1000 / 270;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            speedometerView.reset();
            setSeekBar();
            mAnimator1.start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
