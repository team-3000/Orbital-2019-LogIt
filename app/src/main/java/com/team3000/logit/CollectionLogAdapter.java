package com.team3000.logit;

import android.app.Activity;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class CollectionLogAdapter extends BaseLogAdapter implements Serializable{
    private static final String TAG = "CollectionLogAdapter";
    private List<Pair<Entry, String>> entries;

    public class OnDestroyListener implements Serializable {
        public void onDestroy(int entryPosition) {
            entries.remove(entryPosition);
            CollectionLogAdapter.this.notifyItemRemoved(entryPosition);
        }
    }

    public CollectionLogAdapter(Activity activity, List<Pair<Entry, String>> entries, EntryListener.OnDestroyListener listener) {
        super(activity, listener);
        this.entries = entries;
    }

    @Override
    public void onBindViewHolder(@NonNull EntryHolder holder, int position) {
        Log.i(TAG, "in onBindViewHolder");
        Pair<Entry, String> entryPair = entries.get(position);
        super.fillUpEntryHolder(holder, entryPair.first, entryPair.second, position, new OnDestroyListener());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }
}
