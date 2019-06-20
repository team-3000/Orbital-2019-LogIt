package com.team3000.logit;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.Locale;

public class MonthlyLogActivity extends BaseLogActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String logMonth = String.format(Locale.US, "%s %d", month.toUpperCase(), year);
        tvLogTitle.setText(logMonth);
        entries.clear();
        addToEntriesList(taskDir);
        addToEntriesList(eventDir);
        addToEntriesList(noteDir);
    }

    private void addToEntriesList(String directory) {
        db.collection(directory)
                .whereEqualTo("monthlyLog", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot d : task.getResult()) {
                                entries.add(new Entry(d.getId(), d.getString("type"),
                                        d.getString("title"), d.getString("date"),
                                        d.getString("time"), d.getString("desc")));
                            }
                            Collections.sort(entries);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
