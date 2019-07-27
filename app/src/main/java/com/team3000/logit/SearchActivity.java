package com.team3000.logit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.Locale;

public class SearchActivity extends BaseActivity {
    protected static final int NUM_PAGES = 3;
    protected ViewPager mPager;
    protected PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_base_log, contentFrameLayout);

        String searchQuery = "";
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY);
        }
        getSupportActionBar().setTitle("Search: \"" + searchQuery + "\"");

        mPager = findViewById(R.id.log_pager);
        pagerAdapter = new SearchActivity.SearchPagerAdapter(getSupportFragmentManager(), searchQuery);
        mPager.setAdapter(pagerAdapter);
        TabLayout tabs = findViewById(R.id.logTabLayout);
        tabs.setupWithViewPager(mPager);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first page, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous page.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class SearchPagerAdapter extends FragmentStatePagerAdapter {
        private String searchQuery;

        private SearchPagerAdapter(FragmentManager fm, String searchQuery) {
            super(fm);
            this.searchQuery = searchQuery;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putString("searchQuery", searchQuery);
            String type = (position == 0) ? "note" : ((position == 1) ? "task" : "event");
            bundle.putString("type", type);
            SearchListFragment slfrag = new SearchListFragment();
            slfrag.setArguments(bundle);
            return slfrag;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0 :
                    return "Notes";
                case 1 :
                    return "Tasks";
                case 2 :
                    return "Events";
                default :
                    return null;
            }
        }
    }
}
