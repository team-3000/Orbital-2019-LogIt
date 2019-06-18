package com.team3000.logit;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class CalendarActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_calendar, contentFrameLayout);

        Button btnCalToday = findViewById(R.id.btnCalToday);
        Button btnCalTomorrow = findViewById(R.id.btnCalTomorrow);
        Button btnCalThisMonth = findViewById(R.id.btnCalThisMonth);
        CalendarView calendarView = findViewById(R.id.calendarView);
        Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int day = cal.get(Calendar.DAY_OF_MONTH);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                goToDailyLog(year, month, dayOfMonth, 0);
            }
        });

        btnCalToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDailyLog(year, month, day,0);
            }
        });

        btnCalTomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDailyLog(year, month, day, 1);
            }
        });

//        btnCalThisMonth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentMonth = new Intent(CalendarActivity.this, MonthlyLogActivity.class);
//                intentMonth.putExtra("year", year);
//                intentMonth.putExtra("month", new DateFormatSymbols().getMonths()[month].substring(0, 3));
//                startActivity(intentMonth);
//            }
//        });
    }

    private void goToDailyLog(int year, int month, int day, int dayOffset) {
        String monthName = new DateFormatSymbols().getMonths()[month];
        String monthNameShort = monthName.substring(0, 3);
        Intent intentDaily = new Intent(CalendarActivity.this, DailyLogActivity.class);
        intentDaily.putExtra("year", year);
        intentDaily.putExtra("month", monthNameShort);
        intentDaily.putExtra("day", day + dayOffset);
        startActivity(intentDaily);
    }
}
