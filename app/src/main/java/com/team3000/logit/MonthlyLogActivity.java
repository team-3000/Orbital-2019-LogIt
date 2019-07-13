package com.team3000.logit;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.os.Bundle;

import java.util.Locale;

public class MonthlyLogActivity extends BaseLogActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prevent redirection of the same page when the user click on this month in the drawer
        if (getIntent().getIntExtra("year", 1) == 0) {
            super.currPosition = R.id.nav_this_month;
        }

        String logMonth = String.format(Locale.US, "%s %d", month.toUpperCase(), year);
        getSupportActionBar().setTitle(logMonth);
        mPager = findViewById(R.id.log_pager);
        pagerAdapter = new BaseLogPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
    }

    private class BaseLogPagerAdapter extends FragmentStatePagerAdapter {
      
        private BaseLogPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            String logType = "monthly";
            String entryType;
            if (position == 0) {
                entryType = "note";
            } else if (position == 1) {
                entryType = "task";
            } else {
                entryType = "event";
            }
            String directory = String.format(Locale.US, "users/%s/%s/%d/%s", userId, entryType, year, month);
            bundle.putString("logType", logType);
            bundle.putString("directory", directory);
            bundle.putString("logDate", "");
            bundle.putString("redirect", "monthlylog");
            BaseLogFragment blfrag = new BaseLogFragment();
            blfrag.setArguments(bundle);
            return blfrag;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return (position == 0) ? "Notes" : ((position == 1) ? "Tasks" : "Events");
        }
    }
}
