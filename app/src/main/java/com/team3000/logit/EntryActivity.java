package com.team3000.logit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

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
    private String month;
    private String entryId;
    private String directory;
    private String eisen;
    private int entryPosition;
    private DocumentReference ref;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.entry_nav_allentries:
                    Intent intentList = new Intent(EntryActivity.this, EntryListActivity.class);
                    intentList.putExtra("trackType", type + "Store");
                    startActivity(intentList);
                    return true;
                case R.id.entry_nav_calendar:
                    startActivity(new Intent(EntryActivity.this, CalendarActivity.class));
                    return true;
                default:
                    break;
            }
            return false;
        }
    };

    // Detect change in month and year because it will affect the fetching of data & eventually
    // the display of data
    public class onDateChangeListener implements EntryListener.OnDateChangeListener {
        // Update the entry view accordingly when month and/or year is changed.
        @Override
        public void notifyMonthAndOrYearChanged(Bundle data) {
            Log.i("EntryActivity", "InNotifyMonthChanged");
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            month = data.getString("month");
            directory = String.format("users/%s/%s", userId, data.getString("newDirectory"));
            ref = db.document(directory);
            loadView();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_entry, contentFrameLayout);
        FrameLayout flEntryContent = findViewById(R.id.flEntryContent);
        getLayoutInflater().inflate(R.layout.activity_entry_scrollable_content, flEntryContent);

        navView = findViewById(R.id.bottom_navView);
        tvEntryTitle = findViewById(R.id.tvEntryTitle);
        tvEntryDate = findViewById(R.id.tvEntryDate);
        tvEntryTime = findViewById(R.id.tvEntryTime);
        tvEntryCollection = findViewById(R.id.tvEntryCollection);
        tvEntryExtra = findViewById(R.id.tvEntryExtra);
        tvEntryDesc = findViewById(R.id.tvEntryDesc);
        btnEditEntry = findViewById(R.id.btnEditEntry);
        btnDeleteEntry = findViewById(R.id.btnDeleteEntry);
        type = getIntent().getStringExtra("type");
        month = getIntent().getStringExtra("month");
        entryId = getIntent().getStringExtra("entryId");
        directory = getIntent().getStringExtra("directory");
        ref = db.document(directory);
        String typeCapitalised = type.substring(0, 1).toUpperCase() + type.substring(1);
        getSupportActionBar().setTitle(typeCapitalised);

        // Used for collection log
        entryPosition = getIntent().getIntExtra("entry_position", -1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("EntryActivity", "onStart");
        noteButton.setVisibility(View.GONE);
        taskButton.setVisibility(View.GONE);
        eventButton.setVisibility(View.GONE);
        if ("note".equals(type)) {
            tvEntryExtra.setVisibility(View.GONE);
        }

        loadView();
    }

    // Load the content of the entry view and attach necessary listeners
    private void loadView() {
        ref.get().addOnCompleteListener(task -> {
            DocumentSnapshot doc = task.getResult();
            tvEntryTitle.setText(doc.getString("title"));
            tvEntryDate.setText(doc.getString("date"));
            tvEntryTime.setText(doc.getString("time"));
            tvEntryCollection.setText(doc.getString("collection"));
            if ("task".equals(type)) {
                eisen = doc.getString("eisen");
                String eisenDisplayed = "".equals(eisen) ? "No Priority Assigned" : eisen.toUpperCase();
                tvEntryExtra.setText(eisenDisplayed);
                switch (eisenDisplayed) {
                    case "DO":
                        tvEntryExtra.setTextColor(0xff8bc34a);
                        break;
                    case "DECIDE":
                        tvEntryExtra.setTextColor(Color.BLUE);
                        break;
                    case "DELEGATE":
                        tvEntryExtra.setTextColor(0xffffc107);
                        break;
                    case "ELIMINATE":
                        tvEntryExtra.setTextColor(0xfff44336);
                        break;
                    default:
                        break;
                }
            } else if ("event".equals(type)) {
                String locationReceived = doc.getString("location");
                String location = "".equals(locationReceived) ? "No location" : locationReceived;
                tvEntryExtra.setText(location);
            }
            tvEntryDesc.setText(doc.getString("desc"));
        });

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        btnEditEntry.setOnClickListener(v -> {
            Intent intentEdit = new Intent(EntryActivity.this, EntryFormActivity.class);
            intentEdit.putExtra("type", type);
            intentEdit.putExtra("oriMonth", month);
            intentEdit.putExtra("oriDir", directory);
            intentEdit.putExtra("entryId", entryId);
            intentEdit.putExtra("entry_position", entryPosition);
            intentEdit.putExtra("oriEisen", eisen);
            intentEdit.putExtra("redirect", getIntent().getStringExtra("redirect"));

            EntryManager.onDateChangeListener = new onDateChangeListener();
            startActivity(intentEdit);
        });

        btnDeleteEntry.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EntryActivity.this);
            builder.setMessage("Delete \"" + tvEntryTitle.getText().toString() + "\"?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            EntryManager entryManager = new EntryManager(EntryActivity.this);
                            entryManager.deleteFromTracker(type + "Store", directory);
                            if (!"".equals(eisen)) {
                                entryManager.deleteFromTracker(eisen, directory);
                            }
                            // The EntryActivity will close once item is deleted in Firestore (handled in deleteEntry())
                            entryManager.deleteEntry(ref);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        });
    }
}
