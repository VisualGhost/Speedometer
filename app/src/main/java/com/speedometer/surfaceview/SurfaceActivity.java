package com.speedometer.surfaceview;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.speedometer.R;

public class SurfaceActivity extends AppCompatActivity {

    private static final float SEGMENT_MAX = 270;
    private static final float SEGMENT_1 = 110;
    private static final float SEGMENT_2 = 190;
    private static final float SEGMENT_3 = 240;

    private PieChart mPieChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObservableHolder.Range clockWiseRange = new ObservableHolder.Range(0, SEGMENT_MAX, 2000);
        ObservableHolder.Range counterClockWiseRange = new ObservableHolder.Range(SEGMENT_MAX, SEGMENT_2, 1500);
        mPieChart = new PieChartImpl(this, new ViewControllerImpl(SEGMENT_1, SEGMENT_2, SEGMENT_3, SEGMENT_MAX),
                new ObservableHolder(clockWiseRange, counterClockWiseRange));
        setContentView((View) mPieChart);
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
            mPieChart.startAnimation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
