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
    Button btnCalToday;
    Button btnCalTomorrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_calendar, contentFrameLayout);

        btnCalToday = findViewById(R.id.btnCalToday);
        btnCalTomorrow = findViewById(R.id.btnCalTomorrow);

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String monthName = new DateFormatSymbols().getMonths()[month];
                String monthNameShort = monthName.substring(0, 3);
                Intent intent = new Intent(CalendarActivity.this, DailyLogActivity.class);
                intent.putExtra("year", year);
                intent.putExtra("month", monthNameShort);
                intent.putExtra("day", dayOfMonth);
                startActivity(intent);
            }
        });

        btnCalToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToButtonDest(0);
            }
        });

        btnCalTomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToButtonDest(1);
            }
        });
    }

    private void goToButtonDest(int dayOffset) {
        Calendar cal = Calendar.getInstance();
        Intent intentBtn = new Intent(CalendarActivity.this, DailyLogActivity.class);
        intentBtn.putExtra("year", cal.get(Calendar.YEAR));
        intentBtn.putExtra("month", cal.get(Calendar.MONTH));
        intentBtn.putExtra("day", cal.get(Calendar.DAY_OF_MONTH) + dayOffset);
        startActivity(intentBtn);
    }
}
