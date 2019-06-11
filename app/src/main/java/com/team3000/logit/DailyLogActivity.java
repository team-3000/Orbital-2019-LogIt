package com.team3000.logit;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

public class DailyLogActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        if (contentFrameLayout != null) {
            Toast.makeText(this, "Not null", Toast.LENGTH_SHORT).show();
        }

        getLayoutInflater().inflate(R.layout.activity_daily_log, contentFrameLayout);
    }
}
