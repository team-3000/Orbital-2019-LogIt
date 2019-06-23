package com.team3000.logit;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class BaseLogActivity extends BaseActivity {
    protected static final int NUM_PAGES = 3;
    protected int year;
    protected String month;
    protected int day;
    protected String userId;
    protected ViewPager mPager;
    protected PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the content of the frame layout accordingly.
        // The frame layout serves as a container for the content you want to put.
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_base_log, contentFrameLayout);
        TabLayout tabLayout = findViewById(R.id.logTabLayout);
        tabLayout.setupWithViewPager(mPager);

        userId = user.getUid();
        year = getIntent().getIntExtra("year", 0);
        month = getIntent().getStringExtra("month");
        day = getIntent().getIntExtra("day", 0);
        if (year == 0) {
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            int monthNum = cal.get(Calendar.MONTH);
            String monthName = new DateFormatSymbols().getMonths()[monthNum];
            month = monthName.substring(0, 3);
            day = cal.get(Calendar.DAY_OF_MONTH);
        }
    }
}
