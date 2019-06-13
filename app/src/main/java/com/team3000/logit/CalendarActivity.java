package com.team3000.logit;

import androidx.annotation.NonNull;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.FrameLayout;

import java.text.DateFormatSymbols;

@TargetApi(11)
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

        // Code for Today and Tomorrow buttons using Calendar abstract class for time
    }

    private void goToDailyLog(int year, int month, int dayOfMonth){
        String monthName = new DateFormatSymbols().getMonths()[month];
        String monthNameShort = monthName.substring(0, 3);

        Intent intent = new Intent(CalendarActivity.this, EntryActivity.class);
        intent.putExtra("year", year);
        intent.putExtra("month", monthNameShort);
        intent.putExtra("day", dayOfMonth);
        startActivity(intent);
    }
}
