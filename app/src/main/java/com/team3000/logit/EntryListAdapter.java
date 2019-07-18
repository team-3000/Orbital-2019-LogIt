package com.team3000.logit;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class EntryListAdapter extends BaseLogAdapter {
    private ArrayList<Entry> entries;

    public EntryListAdapter(Activity activity, ArrayList<Entry> entries, EntryHolder.ClickListener clickListener) {
        super(activity, clickListener);
        this.entries = entries;
    }

    @Override
    public void onBindViewHolder(@NonNull EntryHolder holder, int position) {
        Entry currEntry = entries.get(position);
        fillUpEntryHolder(holder, currEntry, currEntry.getId());
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }
}
