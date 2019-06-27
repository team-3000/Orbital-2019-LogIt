package com.team3000.logit;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class CollectionActivity extends BaseActivity {
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.notAtDrawerOptions = true;

        // Load the content of the frame layout accordingly.
        // The frame layout serves as a container for the content you want to put.
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_collection, contentFrameLayout);

        String collection_name = getIntent().getStringExtra("collection_name");
        getSupportActionBar().setTitle(collection_name);

        mPager = findViewById(R.id.viewPager);
        mPager.setAdapter(new CollectionPagerAdapter(getSupportFragmentManager(), collection_name));

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(mPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first page, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous page.
            // mPager.setCurrentItem(mPager.getCurrentItem() - 1);

            // Select the current page instead (try removing -1)
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }
}