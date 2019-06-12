package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.widget.TextView;

public class TaskActivity extends AppCompatActivity {
    TextView tvTaskTitle;
    TextView tvTaskDate;
    TextView tvTaskCollection;
    TextView tvTaskEisen;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_alltasks:
//                    Intent taskListIntent = new Intent(TaskActivity.this, TaskListActivity.class);
//                    startActivity(taskListIntent);
                    return true;
                case R.id.navigation_calendar:
//                    Intent calendarIntent = new Intent(TaskActivity.this, CalendarActivity.class);
//                    startActivity(calendarIntent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        tvTaskTitle = findViewById(R.id.tvTaskTitle);
        tvTaskDate = findViewById(R.id.tvTaskDate);
        tvTaskCollection = findViewById(R.id.tvTaskCollection);
        tvTaskEisen = findViewById(R.id.tvTaskEisen);

        String date = getIntent().getIntExtra("day", 0) + "/" + getIntent().getIntExtra("month", 0) + "/" +
                        getIntent().getIntExtra("year", 0);
        tvTaskDate.setText(date);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
