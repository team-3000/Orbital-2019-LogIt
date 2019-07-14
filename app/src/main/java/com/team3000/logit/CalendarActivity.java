package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

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
        Spinner spnMonthSelect = findViewById(R.id.spnMonthSelect);
        EditText etYearSelect = findViewById(R.id.etYearSelect);
        Button btnGoToMonth = findViewById(R.id.btnGoToMonth);
        CalendarView calendarView = findViewById(R.id.calendarView);
        Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int day = cal.get(Calendar.DAY_OF_MONTH);

        calendarView.setOnDateChangeListener((view, year1, month1, dayOfMonth) -> goToDailyLog(year1, month1, dayOfMonth, 0));

        btnCalToday.setOnClickListener(v -> goToDailyLog(year, month, day,0));

        btnCalTomorrow.setOnClickListener(v -> goToDailyLog(year, month, day, 1));

        ArrayAdapter<CharSequence> mAdapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMonthSelect.setAdapter(mAdapter);
        spnMonthSelect.setSelection(0);

        btnGoToMonth.setOnClickListener(v -> {
            String monthGo = (String) spnMonthSelect.getSelectedItem();
            int yearGo = Integer.parseInt(etYearSelect.getText().toString());
            if ("Month".equals(monthGo) || "".equals(etYearSelect)) {
                Toast.makeText(this, "Please select Month & Year to go to", Toast.LENGTH_SHORT).show();
            } else {
                goToMonthlyLog(monthGo, yearGo);
            }
        });
    }

    private void goToDailyLog(int year, int month, int day, int dayOffset) {
        Intent intentDaily = new Intent(CalendarActivity.this, DailyLogActivity.class);
        intentDaily.putExtra("year", year);
        intentDaily.putExtra("month", new DateFormatSymbols().getMonths()[month].substring(0, 3));
        intentDaily.putExtra("day", day + dayOffset);
        startActivity(intentDaily);
    }

    private void goToMonthlyLog(String month, int year) {
        Intent intentMonth = new Intent(CalendarActivity.this, MonthlyLogActivity.class);
        intentMonth.putExtra("year", year);
        intentMonth.putExtra("month", month);
        startActivity(intentMonth);
    }
}
