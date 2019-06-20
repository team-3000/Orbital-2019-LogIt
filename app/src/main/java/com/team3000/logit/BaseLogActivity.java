package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BaseLogActivity extends BaseActivity {
    protected static final String TAG = "LogActivity";
    protected TextView tvLogTitle;
    protected int year;
    protected String month;
    protected int day;
    private String userId;
    protected String taskDir;
    protected String eventDir;
    protected String noteDir;
    protected List<Entry> entries = new ArrayList<>();
    protected RecyclerView.Adapter mAdapter;
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the content of the frame layout accordingly.
        // The frame layout serves as a container for the content you want to put.
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_base_log, contentFrameLayout);

        tvLogTitle = findViewById(R.id.tvLogTitle);
        userId = user.getUid();
        year = getIntent().getIntExtra("year", 0);
        month = getIntent().getStringExtra("month");
        day = getIntent().getIntExtra("day", 0);
        if (year == 0) {
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            int monthNum = cal.get(Calendar.MONTH);
            String monthName = new DateFormatSymbols().getMonths()[monthNum];
            month = monthName.substring(0, 3);
            day = cal.get(Calendar.DAY_OF_MONTH);
        }
        taskDir = String.format(Locale.US, "users/%s/task/%d/%s", userId, year, month);
        eventDir = String.format(Locale.US, "users/%s/event/%d/%s", userId, year, month);
        noteDir = String.format(Locale.US, "users/%s/note/%d/%s", userId, year, month);

        RecyclerView recyclerView = findViewById(R.id.rvLogRV);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new EntryAdapter(entries);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Entry currItem = entries.get(position);
                String entryType = currItem.getType();
                int entryYear = currItem.getYear();
                String entryMonth = currItem.getMonth();
                String entryId = currItem.getId();
                String directory = String.format(Locale.US, "users/%s/%s/%d/%s/%s", userId, entryType, entryYear, entryMonth, entryId);
                Intent intent = new Intent(BaseLogActivity.this, EntryActivity.class);
                intent.putExtra("type", entryType);
                intent.putExtra("entryId", entryId);
                intent.putExtra("directory", directory);
                startActivity(intent);
                finish();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
}
