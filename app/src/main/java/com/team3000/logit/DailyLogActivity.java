package com.team3000.logit;

import android.os.Bundle;
import android.widget.FrameLayout;

public class DailyLogActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the content of the frame layout accordingly.
        // The frame layout serves as a container for the content you want to put.
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_daily_log, contentFrameLayout);
    }
}
