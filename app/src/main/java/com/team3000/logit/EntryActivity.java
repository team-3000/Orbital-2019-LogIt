package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Locale;

public class EntryActivity extends BaseActivity {
    private TextView tvEntryTitle;
    private TextView tvEntryDate;
    private TextView tvEntryTime;
    private TextView tvEntryCollection;
    private TextView tvEntryEisen;
    private TextView tvEntryDesc;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.entry_nav_alltasks:
//                    Intent taskListIntent = new Intent(EntryActivity.this, TaskListActivity.class);
//                    startActivity(taskListIntent);
                    return true;
                case R.id.entry_nav_calendar:
                    startActivity(new Intent(EntryActivity.this, CalendarActivity.class));
                    return true;
                default:
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_entry, contentFrameLayout);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        tvEntryTitle = findViewById(R.id.tvEntryTitle);
        tvEntryDate = findViewById(R.id.tvEntryDate);
        tvEntryTime = findViewById(R.id.tvEntryTime);
        tvEntryCollection = findViewById(R.id.tvEntryCollection);
        tvEntryEisen = findViewById(R.id.tvEntryEisen);
        tvEntryDesc = findViewById(R.id.tvEntryDesc);

        String date = String.format(Locale.US, "%2d %s %d",
                                    getIntent().getIntExtra("day", 0),
                                    getIntent().getStringExtra("month"),
                                    getIntent().getIntExtra("year", 0));
        tvEntryDate.setText(date);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
