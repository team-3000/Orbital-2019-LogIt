package com.team3000.logit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class EntryFormManager {
    private EntryFormActivity activity;

    public EntryFormManager(EntryFormActivity activity) {
        this.activity = activity;
    }

    protected void preset() {
        activity.database.document(activity.oriDir).get().addOnCompleteListener(task -> {
            DocumentSnapshot doc = task.getResult();
            activity.etFormTitle.setText(doc.getString("title"));
            activity.etFormDate.setText(doc.getString("date"));
            activity.etFormTime.setText(doc.getString("time"));

            activity.curr_collection = doc.getString("collection");
            activity.actvCollection.setText(activity.curr_collection);
            activity.curr_collection_path = doc.getString("collection_path");

            if ("event".equals(activity.type)) {
                activity.etFormLocation.setText(doc.getString("location"));
            }
            activity.etFormDesc.setText(doc.getString("desc"));
            if (doc.get("monthlyLog") == null) {
                activity.cbAddToMonthLog.setChecked(false);
            } else {
                activity.cbAddToMonthLog.setChecked(doc.getBoolean("monthlyLog"));
            }

            if ("task".equals(activity.type)) {
                String eisenTag = doc.getString("eisen");
                if (!"".equals(eisenTag)) {
                    String textToDisplay = eisenTag.substring(0, 1).toUpperCase()
                            + eisenTag.substring(1);
                    activity.eisenField.setText(textToDisplay);
                }
            }
        });
    }

    protected void displayDatePicker(EditText etFormDate) {
        etFormDate.setInputType(InputType.TYPE_NULL);
        etFormDate.setOnClickListener(v -> showDatePicker(etFormDate));
        etFormDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etFormDate.getWindowToken(), 0);
                showDatePicker(etFormDate);
            }
        });
    }

    private void showDatePicker(EditText etFormDate) {
        final Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        DatePickerDialog picker = new DatePickerDialog(activity,
                (view, year1, month1, dayOfMonth) -> {
                    String monthName = new DateFormatSymbols().getMonths()[month1];
                    String monthNameShort = monthName.substring(0, 3);
                    etFormDate.setText(String.format(Locale.US, "%d %s %d", dayOfMonth, monthNameShort, year1));
                }, year, month, day);
        picker.show();
    }

    protected void displayTimePicker(EditText etFormTime) {
        etFormTime.setInputType(InputType.TYPE_NULL);
        etFormTime.setOnClickListener(v -> showTimePicker(etFormTime));
        etFormTime.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etFormTime.getWindowToken(), 0);
                showTimePicker(etFormTime);
            }
        });
    }

    private void showTimePicker(EditText etFormTime) {
        final Calendar cldr = Calendar.getInstance();
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = cldr.get(Calendar.MINUTE);
        TimePickerDialog picker = new TimePickerDialog(activity, android.R.style.Theme_Holo_Light_Dialog,
                (tp, sHour, sMinute) -> {
                    int hour12clock;
                    if (sHour == 0 || sHour == 12) {
                        hour12clock = 12;
                    } else {
                        hour12clock = (sHour > 12) ? (sHour - 12) : sHour;
                    }
                    String meridien = (sHour > 11) ? "pm" : "am";
                    etFormTime.setText(String.format(Locale.US, "%02d:%02d %s", hour12clock, sMinute, meridien));
                }, hour, minutes, false);
        picker.show();
    }

    protected void initializeAutoCompleteTextView() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String dbpath = String.format("users/%s/collections", user.getUid());

        // Retrieve all the collections' names and use them to set up the
        // collection AutoCompleteTextView
        activity.database.collection(dbpath).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> names = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String name = (String) doc.get("name");
                            names.add(name);
                        }

                        String[] container = new String[names.size()];

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity.getApplicationContext(),
                                android.R.layout.simple_dropdown_item_1line, names.toArray(container));
                        activity.actvCollection.setAdapter(adapter);
                    } else {
                        Log.e(activity.TAG, "Fail to load collections for form's AutoCompleteTextView!");
                    }
                });
    }

    protected void fillEntryData(Map<String, Object> entryData, String title, String date, String time,
                               String location, String collection, String eisen, String desc, boolean addToMonthLog) {
        entryData.put("type", activity.type);
        entryData.put("title", title);
        entryData.put("date", date);
        entryData.put("time", time);
        entryData.put("location", location);
        if ("".equals(collection)) {
            entryData.put("collection", "");
            entryData.put("collection_path", "");
        } else {
            entryData.put("collection", collection);
            entryData.put("collection_path", activity.curr_collection_path);
        }

        entryData.put("eisen", eisen);

        if ("".equals(desc)) {
            entryData.put("desc", "");
        } else {
            entryData.put("desc", desc);
        }
        entryData.put("monthlyLog", addToMonthLog);
    }

    protected void redirectToPrecedingPage(String redirect, String type) {
        Intent intentNew = new Intent();

        if (redirect == null) return;

        switch (redirect) {
            case "dailylog":
                intentNew = new Intent(activity, DailyLogActivity.class);
                break;
            case "monthlylog":
                intentNew = new Intent(activity, MonthlyLogActivity.class);
                break;
            case "entrylist":
                intentNew = new Intent(activity, EntryListActivity.class);
                intentNew.putExtra("trackType", type + "Store");
                break;
            default:
                break;
        }
        activity.startActivity(intentNew);
    }
}
