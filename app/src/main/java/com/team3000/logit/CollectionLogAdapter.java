package com.team3000.logit;

import android.app.Activity;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import java.util.List;

public class CollectionLogAdapter extends BaseLogAdapter {
    private static final String TAG = "CollectionLogAdapter";
    private List<Pair<Entry, String>> entries;

    public CollectionLogAdapter(Activity activity, List<Pair<Entry, String>> entries) {
        super(activity);
        this.entries = entries;
    }

    @Override
    public void onBindViewHolder(@NonNull EntryHolder holder, int position) {
        Log.i(TAG, "in onBindViewHolder");
        Pair<Entry, String> entryPair = entries.get(position);
        super.fillUpEntryHolder(holder, entryPair.first, entryPair.second);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void setEntries(List<Pair<Entry, String>> entries) {
        this.entries = entries;
    }
}
