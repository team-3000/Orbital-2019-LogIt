package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Calendar;

public class EntryActivity extends BaseActivity {
    private BottomNavigationView navView;
    private TextView tvEntryTitle;
    private TextView tvEntryDate;
    private TextView tvEntryTime;
    private TextView tvEntryCollection;
    private TextView tvEntryExtra;
    private TextView tvEntryDesc;
    private Button btnEditEntry;
    private Button btnDeleteEntry;
    private String type;
    private String entryId;
    private String directory;
    private DocumentReference ref;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_entry, contentFrameLayout);

        navView = findViewById(R.id.nav_view);
        tvEntryTitle = findViewById(R.id.tvEntryTitle);
        tvEntryDate = findViewById(R.id.tvEntryDate);
        tvEntryTime = findViewById(R.id.tvEntryTime);
        tvEntryCollection = findViewById(R.id.tvEntryCollection);
        tvEntryExtra = findViewById(R.id.tvEntryExtra);
        tvEntryDesc = findViewById(R.id.tvEntryDesc);
        btnEditEntry = findViewById(R.id.btnEditEntry);
        btnDeleteEntry = findViewById(R.id.btnDeleteEntry);
        type = getIntent().getStringExtra("type");
        entryId = getIntent().getStringExtra("entryId");
        directory = getIntent().getStringExtra("directory");
        ref = db.document(directory);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteButton.setVisibility(View.GONE);
        taskButton.setVisibility(View.GONE);
        eventButton.setVisibility(View.GONE);
        if ("note".equals(type)) {
            tvEntryExtra.setVisibility(View.GONE);
        }

        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                tvEntryTitle.setText(doc.getString("title"));
                tvEntryDate.setText(doc.getString("date"));
                tvEntryTime.setText(doc.getString("time"));
                tvEntryCollection.setText(doc.getString("collection"));
                if ("task".equals(type)) {
                    tvEntryExtra.setText(doc.getString("eisen"));
                } else if ("event".equals(type)) {
                    tvEntryExtra.setText(doc.getString("location"));
                }
                tvEntryDesc.setText(doc.getString("desc"));
            }
        });

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        btnEditEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEdit = new Intent(EntryActivity.this, EntryFormActivity.class);
                intentEdit.putExtra("type", type);
                intentEdit.putExtra("oriDir", directory);
                intentEdit.putExtra("entryId", entryId);
                startActivity(intentEdit);
            }
        });

        btnDeleteEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.delete();
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.entry_nav_allentries:
//                    Intent taskListIntent = new Intent(EntryActivity.this, TaskListActivity.class);
//                    startActivity(taskListIntent);
                    return true;
                case R.id.entry_nav_calendar:
                    startActivity(new Intent(EntryActivity.this, CalendarActivity.class));
                    return true;
                case R.id.entry_nav_today:
                    Calendar cal = Calendar.getInstance();
                    Intent intentToday = new Intent(EntryActivity.this, DailyLogActivity.class);
                    intentToday.putExtra("year", cal.get(Calendar.YEAR));
                    intentToday.putExtra("month", cal.get(Calendar.MONTH));
                    intentToday.putExtra("day", cal.get(Calendar.DAY_OF_MONTH));
                    startActivity(intentToday);
                    return true;
                default:
                    break;
            }
            return false;
        }
    };
}
