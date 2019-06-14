package com.team3000.logit;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.FrameLayout;

import java.text.DateFormatSymbols;

public class CalendarActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_calendar, contentFrameLayout);

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                goToDailyLog(year, month, dayOfMonth);
            }
        });

        // Code for Today and Tomorrow buttons using Calendar abstract class for time (same for side drawer)
    }

    private void goToDailyLog(int year, int month, int dayOfMonth){
        String monthName = new DateFormatSymbols().getMonths()[month];
        String monthNameShort = monthName.substring(0, 3);

        // Testing, replace EntryActivity with DailyLogActivity in complete impl
        // Type & Document ID will be passed on click in Daily Log list item
        Intent intent = new Intent(CalendarActivity.this, DailyLogActivity.class);
        intent.putExtra("year", year);
        intent.putExtra("month", monthNameShort);
        intent.putExtra("day", dayOfMonth);
        startActivity(intent);
    }
}
