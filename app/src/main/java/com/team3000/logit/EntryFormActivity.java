package com.team3000.logit;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EntryFormActivity extends AppCompatActivity {
    protected static final String TAG = "EntryFormActivity";
    protected FirebaseFirestore database;
    private FirebaseUser user;
    private EntryFormManager entryFormManager = new EntryFormManager(EntryFormActivity.this);
    private EntryManager entryManager = new EntryManager(EntryFormActivity.this);

    protected EditText etFormTitle;
    protected EditText etFormDate;
    protected EditText etFormTime;
    protected EditText etFormLocation;
    protected AutoCompleteTextView actvCollection;
    protected EditText etFormDesc;
    protected CheckBox cbAddToMonthLog;
    private Button btnFormSubmit;
    protected TextView eisenField;
    protected String oriDir;
    protected String type;
    private String typeCapitalised; // The type string with the first character capitalised
    protected String entryId;
    protected String oriMonth;
    protected String oriEisen;
    protected String redirect;

    // new stuff here
    protected String curr_collection;
    protected String curr_collection_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_form);

        // Firebase part
        database = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Find all the necessary views
        TextView tvFormType = findViewById(R.id.tvFormType);
        etFormTitle = findViewById(R.id.etFormTitle);
        etFormDate = findViewById(R.id.etFormDate);
        etFormTime = findViewById(R.id.etFormTime);
        etFormLocation = findViewById(R.id.etFormLocation);
        actvCollection = findViewById(R.id.actvCollection);
        LinearLayout layoutPriority = findViewById(R.id.layoutPriority);
        eisenField = layoutPriority.findViewById(R.id.eisenField);
        etFormDesc = findViewById(R.id.etFormDesc);
        cbAddToMonthLog = findViewById(R.id.cbAddToMonthLog);
        btnFormSubmit = findViewById(R.id.btnFormSubmit);

        type = getIntent().getStringExtra("type");
        oriDir = getIntent().getStringExtra("oriDir");
        entryId = getIntent().getStringExtra("entryId");
        oriMonth = getIntent().getStringExtra("oriMonth");
        oriEisen = getIntent().getStringExtra("oriEisen");
        redirect = getIntent().getStringExtra("redirect");

        curr_collection_path = "";

        initialiseToolbar();

        // Form logic
        tvFormType.setText(String.format(Locale.US, "Type: %s", type.toUpperCase()));
        if (!"task".equals(type)) {
            layoutPriority.setVisibility(View.GONE);
        }
        if (!"event".equals(type)) {
            etFormLocation.setVisibility(View.GONE);
        }

        if (oriDir != null) {
            entryFormManager.preset();
        }

        // Open a DatePicker when Date EditText or keyboard "next" clicked
        entryFormManager.displayDatePicker(etFormDate);

        // Open a TimePicker when Time EditText clicked
        entryFormManager.displayTimePicker(etFormTime);

        // Show the eisenSelection dialog
        eisenField.setOnClickListener(v -> showEisenSelectionFragment());

        // Initialise the collections AutoCompleteTextView
        entryFormManager.initializeAutoCompleteTextView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnFormSubmit.setOnClickListener(v -> {
            String title = etFormTitle.getText().toString();
            String date = etFormDate.getText().toString();
            String time = etFormTime.getText().toString();
            String location = "event".equals(type) ? etFormLocation.getText().toString() : null;
            final String collection = actvCollection.getText().toString().trim();
            String desc = etFormDesc.getText().toString();
            boolean addToMonthLog = cbAddToMonthLog.isChecked();

            // Handle eisenhower part of the entry form
            String eisen;
            if ("task".equals(type)) {
                String displayedText = eisenField.getText().toString();
                String lowercaseText = displayedText.substring(0, 1).toLowerCase()
                        + displayedText.substring(1);

                eisen = "select Priority".equals(lowercaseText) ? "" : lowercaseText;

                Log.i(TAG, eisen);
            } else {
                eisen = "";
            }

            // Handle form submission
            Bundle info = new Bundle();
            info.putString("title", title);
            info.putString("date", date);
            info.putString("time", time);
            info.putString("collectionName", collection);

            // Submit the entry into the database if it's valid
            if (checkIfFormIsValid(info)) {
                final Map<String, Object> entryData = new HashMap<>();
                entryFormManager.fillEntryData(entryData, title, date, time, location, collection, eisen, desc, addToMonthLog);

                String[] dateArr = date.split(" ");
                final int year = Integer.parseInt(dateArr[2]);
                final String month = dateArr[1];

                final String dbPath_middle = String.format(Locale.US, "%s/%d/%s", type, year, month);
                final String dbPath = String.format(Locale.US, "users/%s/%s", user.getUid(), dbPath_middle);
                CollectionReference ref = database.collection(dbPath);

                if (oriDir == null) {
                    entryManager.addEntry(ref, entryFormManager, entryData, collection, type, dbPath_middle, dbPath, eisen, redirect);
                } else {
                    entryManager.updateEntry(ref, entryId, type, oriDir, entryData, dbPath_middle, dbPath, collection,
                            curr_collection, curr_collection_path, eisen, oriEisen, month, oriMonth, database);
                }

                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        entryFormManager.redirectToPrecedingPage(redirect, type);
    }

    // Set the logic of the back button on toolbar so that user can
    // navigate back to the previous activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // When the back button on the action bar is clicked
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initialiseToolbar() {
        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the title of the toolbar accoridngly
        typeCapitalised = type.substring(0, 1).toUpperCase() + type.substring(1);
        getSupportActionBar().setTitle("New/Edit " + typeCapitalised);
    }

    private void showEisenSelectionFragment() {
        // Handle Fragment transaction & backstack stuff
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog fragment
        // Note that the setOnDestroyListener must be chained together as it returns
        // a new eisenselectionfragment
        EisenSelectionFragment fragment = new EisenSelectionFragment()
                .setOnDestroyListener(eisenTag -> eisenField.setText(eisenTag));

        fragment.show(getSupportFragmentManager(), "dialog");
    }

    private boolean checkIfFormIsValid(Bundle entryInfo) {
        String title = entryInfo.getString("title");
        String date = entryInfo.getString("date");
        String time = entryInfo.getString("time");
        String collectionName = entryInfo.getString("collectionName");

        // Clear all the previous errors first
        etFormTitle.setError(null);
        etFormDate.setError(null);
        etFormTime.setError(null);
        actvCollection.setError(null);

        // Check if there's any error
        if ("".equals(title)) etFormTitle.setError("Please fill in this field");
        if ("".equals(date)) etFormDate.setError("Please fill in this field");
        if ("".equals(time)) etFormTime.setError("Please fill in this field");
        if (checkIfCollectionNameIsInvalid(collectionName)) actvCollection.setError("Collection name cannot contain the following characters . $ [ ] # / \\");

        boolean isValid = (etFormTitle.getError() == null && etFormDate.getError() == null &&
                           etFormTime.getError() == null && actvCollection.getError() == null);

        return isValid;
    }

    // covert the collection name string into an array and check it to prevent multiple traversing
    // through the string
    private boolean checkIfCollectionNameIsInvalid(String name) {
        boolean isInvalid = false;

        if (name != null) {
            char[] characters = name.toCharArray();
            for (char c : characters) {
                isInvalid = (c == '.' || c == '$' ||
                        c == '[' || c == ']' ||
                        c == '#' || c == '/' ||
                        c == '\\');
                if (isInvalid) break;
            }
        }

        return isInvalid;
    }
}
