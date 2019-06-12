package com.team3000.logit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;

public class CalendarActivity extends AppCompatActivity {
    CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

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
