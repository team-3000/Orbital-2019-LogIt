package com.team3000.logit;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.os.Bundle;

import java.util.Locale;

public class MonthlyLogActivity extends BaseLogActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String logMonth = String.format(Locale.US, "%s %d", month.toUpperCase(), year);
        getSupportActionBar().setTitle(logMonth);
        mPager = findViewById(R.id.log_pager);
        pagerAdapter = new BaseLogPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
    }

    private class BaseLogPagerAdapter extends FragmentStatePagerAdapter {
        public BaseLogPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            String logType = "monthly";
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
            bundle.putString("logDate", "");
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
