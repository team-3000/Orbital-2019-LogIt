package com.team3000.logit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.Locale;

public class DailyLogActivity extends BaseLogActivity {
    private String logDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logDate = String.format(Locale.US, "%d %s %d", day, month, year);
        getSupportActionBar().setTitle(logDate);
        mPager = findViewById(R.id.log_pager);
        pagerAdapter = new BaseLogPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class BaseLogPagerAdapter extends FragmentStatePagerAdapter {
        public BaseLogPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            String logType = "daily";
            String entryType;
            if (position == 0) {
                entryType = "task";
            } else if (position == 1) {
                entryType = "event";
            } else {
                entryType = "note";
            }
            String directory = String.format(Locale.US, "users/%s/%s/%d/%s", userId, entryType, year, month);
            String heading = entryType.substring(0, 1).toUpperCase() + entryType.substring(1) + "s";
            bundle.putString("heading", heading);
            bundle.putString("logType", logType);
            bundle.putString("directory", directory);
            bundle.putString("logDate", logDate);
            BaseLogFragment blfrag = new BaseLogFragment();
            blfrag.setArguments(bundle);
            return blfrag;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
