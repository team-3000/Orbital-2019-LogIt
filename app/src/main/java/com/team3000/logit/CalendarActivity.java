package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class CalendarActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.currPosition = R.id.nav_calendar;

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_calendar, contentFrameLayout);

        Button btnCalToday = findViewById(R.id.btnCalToday);
        Button btnCalTomorrow = findViewById(R.id.btnCalTomorrow);
        Button btnCalLastMonth = findViewById(R.id.btnCalLastMonth);
        Button btnCalThisMonth = findViewById(R.id.btnCalThisMonth);
        Button btnCalNextMonth = findViewById(R.id.btnCalNextMonth);
        CalendarView calendarView = findViewById(R.id.calendarView);
        Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int day = cal.get(Calendar.DAY_OF_MONTH);

        calendarView.setOnDateChangeListener((view, year1, month1, dayOfMonth) -> goToDailyLog(year1, month1, dayOfMonth, 0));

        btnCalToday.setOnClickListener(v -> goToDailyLog(year, month, day,0));

        btnCalTomorrow.setOnClickListener(v -> goToDailyLog(year, month, day, 1));

        btnCalLastMonth.setOnClickListener(v -> {
            int lastMonth;
            int adjYear;
            if (month == 1) {
                lastMonth = 12;
                adjYear = year - 1;
            } else {
                lastMonth = month - 1;
                adjYear = year;
            }
            goToMonthlyLog(adjYear, lastMonth);
        });

        btnCalThisMonth.setOnClickListener(v -> goToMonthlyLog(year, month));

        btnCalNextMonth.setOnClickListener(v -> {
            int nextMonth;
            int adjYear;
            if (month == 12) {
                nextMonth = 1;
                adjYear = year + 1;
            } else {
                nextMonth = month + 1;
                adjYear = year;
            }
            goToMonthlyLog(adjYear, nextMonth);
        });
    }

    private void goToDailyLog(int year, int month, int day, int dayOffset) {
        Intent intentDaily = new Intent(CalendarActivity.this, DailyLogActivity.class);
        intentDaily.putExtra("year", year);
        intentDaily.putExtra("month", new DateFormatSymbols().getMonths()[month].substring(0, 3));
        intentDaily.putExtra("day", day + dayOffset);
        startActivity(intentDaily);
    }

    private void goToMonthlyLog(int year, int month) {
        Intent intentMonth = new Intent(CalendarActivity.this, MonthlyLogActivity.class);
        intentMonth.putExtra("year", year);
        intentMonth.putExtra("month", new DateFormatSymbols().getMonths()[month].substring(0, 3));
        startActivity(intentMonth);
    }
}
