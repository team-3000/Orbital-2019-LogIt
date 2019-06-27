package com.team3000.logit;

import android.app.Activity;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class EntryListAdapter extends BaseLogAdapter {
    private ArrayList<Entry> entries;

    public EntryListAdapter(Activity activity, ArrayList<Entry> entries) {
        super(activity);
        this.entries = entries;
    }

    @Override
    public void onBindViewHolder(@NonNull EntryHolder holder, int position) {
        Entry currEntry = entries.get(position);
        fillUpEntryHolder(holder, currEntry, currEntry.getId());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }
}
