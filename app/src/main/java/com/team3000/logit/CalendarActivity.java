package com.team3000.logit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.FrameLayout;

@TargetApi(11)
public class CalendarActivity extends BaseActivity {
    CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_calendar, contentFrameLayout);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                goToDailyLog(year, month, dayOfMonth);
            }
        });

        // Code for Today and Tomorrow buttons using Calendar abstract class for time
    }

    private void goToDailyLog(int year, int month, int dayOfMonth){
        Intent intent = new Intent(CalendarActivity.this, TaskActivity.class);
        intent.putExtra("year", year);
        intent.putExtra("month", month);
        intent.putExtra("day", dayOfMonth);
        startActivity(intent);
    }
}
