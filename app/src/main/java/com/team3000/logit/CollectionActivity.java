package com.team3000.logit;

import android.os.Bundle;
import android.widget.FrameLayout;

public class CollectionActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_collection_list, contentFrame);

        getSupportActionBar().setTitle(getIntent().getStringExtra("collection_name"));

        // Test out tab layout and pagerview
    }
}
