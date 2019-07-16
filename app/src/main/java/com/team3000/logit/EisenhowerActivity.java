package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

public class EisenhowerActivity extends BaseActivity {
    private TextView tVDo;
    private TextView tVDecide;
    private TextView tVDelegate;
    private TextView tVEliminate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.currPosition = R.id.nav_eisen;

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_eisenhower, contentFrameLayout);
        tVDo = findViewById(R.id.Do);
        tVDecide = findViewById(R.id.Decide);
        tVDelegate = findViewById(R.id.Delegate);
        tVEliminate = findViewById(R.id.Eliminate);
    }

    @Override
    protected void onStart() {
        super.onStart();

        tVDo.setOnClickListener(v -> loadEntryList("do"));
        tVDecide.setOnClickListener(v -> loadEntryList("decide"));
        tVDelegate.setOnClickListener(v -> loadEntryList("delegate"));
        tVEliminate.setOnClickListener(v -> loadEntryList("eliminate"));
    }

    private void loadEntryList(String trackType) {
        Intent intent = new Intent(EisenhowerActivity.this, EntryListActivity.class);
        intent.putExtra("trackType", trackType);
        startActivity(intent);
    }
}
