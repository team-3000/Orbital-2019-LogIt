package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormatSymbols;

public class CalendarActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.currPosition = R.id.nav_calendar;

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_calendar, contentFrameLayout);

        Spinner spnMonthSelect = findViewById(R.id.spnMonthSelect);
        EditText etYearSelect = findViewById(R.id.etYearSelect);
        Button btnGoToMonth = findViewById(R.id.btnGoToMonth);
        EditText etDaySelect = findViewById(R.id.etDaySelect);
        Button btnGoToDay = findViewById(R.id.btnGoToDay);
        CalendarView calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener((view, year1, month1, dayOfMonth) -> goToDailyLog(year1,
                new DateFormatSymbols().getMonths()[month1].substring(0, 3), dayOfMonth));

        ArrayAdapter<CharSequence> mAdapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMonthSelect.setAdapter(mAdapter);
        spnMonthSelect.setSelection(0);

        btnGoToMonth.setOnClickListener(v -> {
            String monthGo = (String) spnMonthSelect.getSelectedItem();
            String yearGoString = etYearSelect.getText().toString();
            if ("Month".equals(monthGo) || "".equals(yearGoString)) {
                Toast.makeText(this, "Please input Month & Year", Toast.LENGTH_SHORT).show();
            } else {
                goToMonthlyLog(monthGo, Integer.parseInt(yearGoString));
            }
        });

        btnGoToDay.setOnClickListener(v -> {
            String monthGo = (String) spnMonthSelect.getSelectedItem();
            String yearGoString = etYearSelect.getText().toString();
            String dayGoString = etDaySelect.getText().toString();
            int dayGo = "".equals(dayGoString) ? 0 : Integer.parseInt(dayGoString);
            if ("Month".equals(monthGo) || "".equals(yearGoString)) {
                Toast.makeText(this, "Please input Month & Year", Toast.LENGTH_SHORT).show();
            } else if (isValidDay(monthGo, dayGo)) {
                goToDailyLog(Integer.parseInt(yearGoString), monthGo, dayGo);
            } else {
                Toast.makeText(this, "Please input a valid Day", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToDailyLog(int year, String month, int day) {
        Intent intentDaily = new Intent(CalendarActivity.this, DailyLogActivity.class);
        intentDaily.putExtra("year", year);
        intentDaily.putExtra("month", month);
        intentDaily.putExtra("day", day);
        startActivity(intentDaily);
    }

    private void goToMonthlyLog(String month, int year) {
        Intent intentMonth = new Intent(CalendarActivity.this, MonthlyLogActivity.class);
        intentMonth.putExtra("year", year);
        intentMonth.putExtra("month", month);
        startActivity(intentMonth);
    }

    private boolean isValidDay(String month, int day) {
        return (day > 0) &&
                // 31-day months
                ((("Jan".equals(month) || "Mar".equals(month) || "May".equals(month) || "Jul".equals(month) ||
                        "Aug".equals(month) || "Oct".equals(month) || "Dec".equals(month)) && day < 31) ||
                        // 30-day months
                        (("April".equals(month) || "Jun".equals(month) || "Sep".equals(month) ||
                                "Nov".equals(month)) && day < 30) ||
                        // Feb 28 days
                        ("Feb".equals(month) && day < 28));
    }
}
