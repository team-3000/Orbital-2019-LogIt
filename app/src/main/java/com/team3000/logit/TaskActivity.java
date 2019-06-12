package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

public class TaskActivity extends BaseActivity {
    protected TextView tvTaskTitle;
    protected TextView tvTaskDate;
    protected TextView tvTaskTime;
    protected TextView tvTaskCollection;
    protected TextView tvTaskEisen;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.entry_nav_alltasks:
//                    Intent taskListIntent = new Intent(TaskActivity.this, TaskListActivity.class);
//                    startActivity(taskListIntent);
                    return true;
                case R.id.entry_nav_calendar:
                    startActivity(new Intent(TaskActivity.this, CalendarActivity.class));
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
        getLayoutInflater().inflate(R.layout.activity_task, contentFrameLayout);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        tvTaskTitle = findViewById(R.id.tvTaskTitle);
        tvTaskDate = findViewById(R.id.tvTaskDate);
        tvTaskTime = findViewById(R.id.tvTaskTime);
        tvTaskCollection = findViewById(R.id.tvTaskCollection);
        tvTaskEisen = findViewById(R.id.tvTaskTime);

        String date = getIntent().getIntExtra("day", 0) + "/" + getIntent().getIntExtra("month", 0) + "/" +
                        getIntent().getIntExtra("year", 0);
        tvTaskDate.setText(date);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
