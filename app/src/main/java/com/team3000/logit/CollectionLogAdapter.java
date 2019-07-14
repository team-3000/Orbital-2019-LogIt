package com.team3000.logit;

import android.app.Activity;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class CollectionLogAdapter extends BaseLogAdapter implements Serializable{
    private static final String TAG = "CollectionLogAdapter";
    // private List<Pair<Entry, String>> entries;
    private List<EntryPair> entries;

    public CollectionLogAdapter(Activity activity, List<EntryPair> entries) {
        super(activity);
        this.entries = entries;
    }

    @Override
    public void onBindViewHolder(@NonNull EntryHolder holder, int position) {
        Log.i(TAG, "in CollectionLogAdapter onBindViewHolder");
        // Pair<Entry, String> entryPair = entries.get(position);
        EntryPair entryPair = entries.get(position);
        // super.fillUpEntryHolder(holder, entryPair.first, entryPair.second);
        super.fillUpEntryHolder(holder, entryPair.getEntry(), entryPair.getEntryId());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }
}
