package com.team3000.logit;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.Locale;

public class DailyLogActivity extends BaseLogActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String logDate = String.format(Locale.US, "%d %s %d", day, month, year);
        tvLogTitle.setText(String.format(Locale.US, "Daily Log: %s", logDate));
        entries.clear();
        addToEntriesList(taskDir, logDate);
        addToEntriesList(eventDir, logDate);
        addToEntriesList(noteDir, logDate);
    }

    private void addToEntriesList(String directory, String logDate) {
        db.collection(directory)
                .whereEqualTo("date", logDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot d : task.getResult()) {
                                entries.add(new Entry(d.getId(), d.getString("type"),
                                        d.getString("title"), d.getString("date"),
                                        d.getString("time"), d.getString("desc")));
                                Collections.sort(entries);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
