package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;

public class EisenhowerActivity extends BaseActivity {
    private Button btnDo;
    private Button btnDecide;
    private Button btnDelegate;
    private Button btnEliminate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.currPosition = R.id.nav_eisen;

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_eisenhower, contentFrameLayout);
        btnDo = findViewById(R.id.btnDo);
        btnDecide = findViewById(R.id.btnDecide);
        btnDelegate = findViewById(R.id.btnDelegate);
        btnEliminate = findViewById(R.id.btnEliminate);
    }

    @Override
    protected void onStart() {
        super.onStart();

        btnDo.setOnClickListener(v -> loadEntryList("do"));
        btnDecide.setOnClickListener(v -> loadEntryList("decide"));
        btnDelegate.setOnClickListener(v -> loadEntryList("delegate"));
        btnEliminate.setOnClickListener(v -> loadEntryList("eliminate"));
    }

    private void loadEntryList(String trackType) {
        Intent intent = new Intent(EisenhowerActivity.this, EntryListActivity.class);
        intent.putExtra("trackType", trackType);
        startActivity(intent);
    }
}
