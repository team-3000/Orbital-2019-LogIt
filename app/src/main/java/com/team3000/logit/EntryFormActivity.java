package com.team3000.logit;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EntryFormActivity extends BaseActivity {
    private EditText etFormTitle;
    private EditText etFormDate;
    private EditText etFormTime;
    private EditText etFormLocation;
    private AutoCompleteTextView actvCollection;
    private Spinner spnFormEisen;
    private EditText etFormDesc;
//    private CheckBox cbAddToMonthLog;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_entry_form, contentFrameLayout);

        TextView tvFormType = findViewById(R.id.tvFormType);
        etFormTitle = findViewById(R.id.etFormTitle);
        etFormDate = findViewById(R.id.etFormDate);
        etFormTime = findViewById(R.id.etFormTime);
        etFormLocation = findViewById(R.id.etFormLocation);
        actvCollection = findViewById(R.id.actvCollection);
        LinearLayout layoutPriority = findViewById(R.id.layoutPriority);
        spnFormEisen = findViewById(R.id.spnFormEisen);
        etFormDesc = findViewById(R.id.etFormDesc);
//        cbAddToMonthLog = findViewById(R.id.cbAddToMonthLog);
        Button btnFormSubmit = findViewById(R.id.btnFormSubmit);
        final String type = getIntent().getStringExtra("type");
        final String oriDir = getIntent().getStringExtra("oriDir");
        final String entryId = getIntent().getStringExtra("entryId");

        tvFormType.setText(String.format(Locale.US, "Type: %s", type.toUpperCase()));
        if (!"task".equals(type)) {
            layoutPriority.setVisibility(View.GONE);
        }
        if (!"event".equals(type)) {
            etFormLocation.setVisibility(View.GONE);
        }

        if (oriDir != null ) {
            db.document(oriDir).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot doc = task.getResult();
                    etFormTitle.setText(doc.getString("title"));
                    etFormDate.setText(doc.getString("date"));
                    etFormTime.setText(doc.getString("time"));
                    actvCollection.setText(doc.getString("collection"));
                    if (type.equals("event")) {
                        etFormLocation.setText(doc.getString("location"));
                    }
                    etFormDesc.setText(doc.getString("desc"));
                }
            });
        }

        // Open a DatePicker when Date EditText clicked
        etFormDate.setOnClickListener(new View.OnClickListener() {
            @Override
            @TargetApi(24)
            public void onClick(View v) {
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
                                etFormDate.setText(String.format(Locale.US, "%02d %s %d", dayOfMonth, monthNameShort, year));
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        // Open a TimePicker when Time EditText clicked
        etFormTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        btnFormSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etFormTitle.getText().toString();
                String date = etFormDate.getText().toString();
                String time = etFormTime.getText().toString();
                String location = etFormLocation.getText().toString();
                String collection = actvCollection.getText().toString();
                String eisen = spnFormEisen.getSelectedItem().toString();
                String desc = etFormDesc.getText().toString();

                if ("".equals(title) || "".equals(date) || "".equals(time)) {
                    Toast.makeText(EntryFormActivity.this, "Please fill in required fields", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, String> entryData = new HashMap<>();
                    entryData.put("type", type);
                    entryData.put("title", title);
                    entryData.put("date", date);
                    entryData.put("time", time);
                    if ("event".equals(type)) {
                        entryData.put("location", location);
                    }
                    if ("".equals(collection)) {
                        entryData.put("collection", "");
                    } else {
                        entryData.put("collection", collection);
                    }
                    if ("task".equals(type)) {
                        entryData.put("eisen", eisen);
                    }
                    if ("".equals(desc)) {
                        entryData.put("desc", "");
                    } else {
                        entryData.put("desc", desc);
                    }

                    String[] dateArr = date.split(" ");
                    int year = Integer.parseInt(dateArr[2]);
                    String month = dateArr[1];
                    String dbPath = String.format(Locale.US, "users/%s/%s/%d/%s", user.getUid(), type, year, month);
                    CollectionReference ref = db.collection(dbPath);

                    if (oriDir == null) {
                        ref.add(entryData);
                    } else {
                        ref.document(entryId).set(entryData);
                    }

//                if (cbAddToMonthLog.isChecked()) {
                    // Add to monthly log
//                }
                    EntryFormActivity.this.finish();
                }
            }
        });
    }
}
