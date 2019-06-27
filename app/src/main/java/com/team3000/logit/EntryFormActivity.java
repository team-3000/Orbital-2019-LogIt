package com.team3000.logit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EntryFormActivity extends AppCompatActivity {
    private static final String TAG = "EntryFormActivity";
    private FirebaseFirestore database;
    private FirebaseUser user;

    private EditText etFormTitle;
    private EditText etFormDate;
    private EditText etFormTime;
    private EditText etFormLocation;
    private AutoCompleteTextView actvCollection;
    private Spinner spnFormEisen;
    private EditText etFormDesc;
    private CheckBox cbAddToMonthLog;
    private Button btnFormSubmit;
    private String oriDir;
    private String type;
    private String typeCapitalised; // The type string with the first character capitalised
    private String entryId;
    private String oriMonth;

    // new stuff here
    private String curr_collection;
    private String curr_collection_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_form);
        /*
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_entry_form, contentFrameLayout);
        */

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
        spnFormEisen = findViewById(R.id.spnFormEisen);
        etFormDesc = findViewById(R.id.etFormDesc);
        cbAddToMonthLog = findViewById(R.id.cbAddToMonthLog);
        btnFormSubmit = findViewById(R.id.btnFormSubmit);
        type = getIntent().getStringExtra("type");
        oriDir = getIntent().getStringExtra("oriDir");
        entryId = getIntent().getStringExtra("entryId");
        oriMonth = getIntent().getStringExtra("oriMonth");

        curr_collection_path = ""; // new stuff here

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
            preset(type, etFormTitle, etFormDate, etFormTime, actvCollection, etFormLocation, etFormDesc, cbAddToMonthLog);
        }

        // Open a DatePicker when Date EditText or keyboard "next" clicked
        etFormDate.setInputType(InputType.TYPE_NULL);
        etFormDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(etFormDate);
            }
        });
        etFormDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etFormDate.getWindowToken(), 0);
                    showDatePicker(etFormDate);
                }
            }
        });

        // Open a TimePicker when Time EditText clicked
        etFormTime.setInputType(InputType.TYPE_NULL);
        etFormTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(etFormTime);
            }
        });
        etFormTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etFormTime.getWindowToken(), 0);
                    showTimePicker(etFormTime);
                }
            }
        });

        // Initialise the collections AutoCompleteTextView
        initializeAutoCompleteTextView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnFormSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etFormTitle.getText().toString();
                String date = etFormDate.getText().toString();
                String time = etFormTime.getText().toString();
                String location = "event".equals(type) ? etFormLocation.getText().toString() : null;
                final String collection = actvCollection.getText().toString();
                String eisen = "task".equals(type) ? spnFormEisen.getSelectedItem().toString() : null;
                String desc = etFormDesc.getText().toString();
                boolean addToMonthLog = cbAddToMonthLog.isChecked();

                if ("".equals(title) || "".equals(date) || "".equals(time)) {
                    Toast.makeText(EntryFormActivity.this, "Please fill in required fields", Toast.LENGTH_SHORT).show();
                } else {
                    final Map<String, Object> entryData = new HashMap<>();
                    fillEntryData(entryData, title, date, time, location, collection, eisen, desc, addToMonthLog);

                    String[] dateArr = date.split(" ");
                    final int year = Integer.parseInt(dateArr[2]);
                    final String month = dateArr[1];

                    final String dbPath_middle = String.format(Locale.US, "%s/%d/%s", type, year, month);
                    final String dbPath = String.format(Locale.US, "users/%s/%s", user.getUid(), dbPath_middle);

                    // final String dbPath = String.format(Locale.US, "users/%s/%s/%d/%s", user.getUid(), type, year, month);
                    CollectionReference ref = database.collection(dbPath);

                    if (oriDir == null) {
                        ref.add(entryData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                // Add entry into collection if collection is specified
                                if (task.isSuccessful()) {
                                    DocumentReference doc = task.getResult();
                                    String docID = doc.getId();
                                    String docPath = String.format(Locale.US, "%s/%s", dbPath_middle, docID);
                                    /*
                                    String docPath = String.format(Locale.US, "%d/%s/%s", year, month
                                                            , docID);
                                    */
                                    EntryManager manager = new EntryManager(EntryFormActivity.this);
                                    manager.addIntoCollection(collection, type, docPath, doc);
                                    String entryPath = String.format("%s/%s", dbPath, docID);
                                    manager.updateEntryTracker(type, entryPath, "No oriDir");
                                }
                            }
                        });
                    } else {
                        final DocumentReference doc = ref.document(entryId);
                        doc.set(entryData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String docPath = String.format(Locale.US, "%s/%s",
                                        dbPath_middle, entryId);
                                EntryManager manager = new EntryManager(EntryFormActivity.this);
                                manager.addIntoCollectionForExistingDoc(collection, curr_collection, type, docPath, doc,
                                        curr_collection_path);
                                String entryPath = String.format("%s/%s", dbPath, entryId);
                                manager.updateEntryTracker(type, entryPath, oriDir);
                            }
                        });
                        if (!month.equals(oriMonth)) {
                            database.document(oriDir).delete();
                            Intent intent = new Intent(EntryFormActivity.this, EntryActivity.class);
                            intent.putExtra("type", type);
                            intent.putExtra("month", month);
                            intent.putExtra("entryId", entryId);
                            intent.putExtra("directory", String.format(Locale.US, "%s/%s", dbPath, entryId));
                            startActivity(intent);
                        }

                        /*
                        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    String old_collection_path = (String) task.getResult().get("collection_path");


                                }
                            }
                        });
                        */

                        /*
                        doc.set(entryData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                new EntryManager(EntryFormActivity.this, TAG)
                                        .addIntoCollectionForExistingDoc(collection, type, dbPath, doc);
                            }
                        });
                        Log.e(TAG, "The path is " + doc.getPath());
                        */
                    }

//                if (cbAddToMonthLog.isChecked()) {
                    // Add to monthly log
//                }

                    EntryFormActivity.this.finish();
                    Toast.makeText(EntryFormActivity.this, typeCapitalised + " added", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
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

    private void preset(final String type, final EditText etFormTitle, final EditText etFormDate, final EditText etFormTime,
                        final AutoCompleteTextView actvCollection, final EditText etFormLocation, final EditText etFormDesc,
                        final CheckBox cbAddToMonthLog) {
        database.document(oriDir).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override

            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                etFormTitle.setText(doc.getString("title"));
                etFormDate.setText(doc.getString("date"));
                etFormTime.setText(doc.getString("time"));

                // New stuff here
                curr_collection = doc.getString("collection");
                actvCollection.setText(curr_collection);
                curr_collection_path = doc.getString("collection_path");

                if ("event".equals(type)) {
                    etFormLocation.setText(doc.getString("location"));
                }
                etFormDesc.setText(doc.getString("desc"));
                if (doc.get("monthlyLog") == null) {
                    cbAddToMonthLog.setChecked(false);
                } else {
                    cbAddToMonthLog.setChecked(doc.getBoolean("monthlyLog"));
                }
            }
        });
    }

    private void showDatePicker(final EditText etFormDate) {
        final Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        DatePickerDialog picker = new DatePickerDialog(EntryFormActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String monthName = new DateFormatSymbols().getMonths()[month];
                        String monthNameShort = monthName.substring(0, 3);
                        etFormDate.setText(String.format(Locale.US, "%d %s %d", dayOfMonth, monthNameShort, year));
                    }
                }, year, month, day);
        picker.show();
    }

    private void showTimePicker(final EditText etFormTime) {
        final Calendar cldr = Calendar.getInstance();
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = cldr.get(Calendar.MINUTE);
        TimePickerDialog picker = new TimePickerDialog(EntryFormActivity.this, android.R.style.Theme_Holo_Light_Dialog,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        int hour12clock;
                        if (sHour == 0 || sHour == 12) {
                            hour12clock = 12;
                        } else {
                            hour12clock = (sHour > 12) ? (sHour - 12) : sHour;
                        }
                        String meridien = (sHour > 11) ? "pm" : "am";
                        etFormTime.setText(String.format(Locale.US, "%02d:%02d %s", hour12clock, sMinute, meridien));
                    }
                }, hour, minutes, false);
        picker.show();
    }

    private void initializeAutoCompleteTextView() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String dbpath = String.format("users/%s/collections", user.getUid());

        // Retrieve all the collections' name and use them to set up the
        // collection AutoCompleteTextView
        database.collection(dbpath).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> names = new ArrayList<>();

                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String name = (String) doc.get("name");
                                names.add(name);
                            }

                            String[] container = new String[names.size()];

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext()
                                    , android.R.layout.simple_dropdown_item_1line, names.toArray(container));
                            actvCollection.setAdapter(adapter);
                        } else {
                            Log.e(TAG, "Fail to load collections for form's AutoCompleteTextView!");
                        }
                    }
                });
    }

    private void fillEntryData(Map<String, Object> entryData, String title, String date, String time,
                               String location, String collection, String eisen, String desc, boolean addToMonthLog) {
        entryData.put("type", type);
        entryData.put("title", title);
        entryData.put("date", date);
        entryData.put("time", time);
        entryData.put("location", location);
        // entryData.put("collection_path", curr_collection_path);
        if ("".equals(collection)) {
            entryData.put("collection", "");
            entryData.put("collection_path", "");
        } else {
            entryData.put("collection", collection);
            entryData.put("collection_path", curr_collection_path);
        }
        entryData.put("eisen", eisen);
        if ("".equals(desc)) {
            entryData.put("desc", "");
        } else {
            entryData.put("desc", desc);
        }
        entryData.put("monthlyLog", addToMonthLog);
    }
}
