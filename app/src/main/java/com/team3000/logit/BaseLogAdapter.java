package com.team3000.logit;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public abstract class BaseLogAdapter extends MultiSelectAdapter<EntryHolder> {
    private static final String TAG = "BaseLogAdapter";
    private Activity activity;
    private String userId;
    protected EntryListener.OnUpdateListener onUpdateListener;
    private EntryHolder.ClickListener clickListener;

    public BaseLogAdapter(Activity activity, EntryHolder.ClickListener clickListener) {
        this.activity = activity;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.clickListener = clickListener;
    }

    public BaseLogAdapter setOnUpdateListener(EntryListener.OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
        return this;
    }

    @Override
    public EntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new EntryHolder(mView, clickListener);
    }

    public void fillUpEntryHolder(EntryHolder holder, Entry entry, String entryId) {
        Log.i(TAG, "In fillUpEntryHolder");
        Log.i(TAG, entryId);
        holder.tvListTitle.setText(entry.getTitle());
        holder.tvListDate.setText(entry.getDate());
        holder.tvListTime.setText(entry.getTime());
        holder.tvListDesc.setText(entry.getDesc());
        // final DocumentSnapshot doc = getSnapshots().getSnapshot(holder.getAdapterPosition());
        holder.mView.setOnClickListener(v -> {
            Log.i(TAG, "Attaching onDestroyListener");
            EntryManager.setOnUpdateListener(onUpdateListener); // For collection log

            String entryType = entry.getType();
            int entryYear = entry.getYear();
            String entryMonth = entry.getMonth();
            // String entryId = doc.getId();
            String directory = String.format(Locale.US, "users/%s/%s/%d/%s/%s", userId, entryType, entryYear, entryMonth, entryId);
            Intent entryIntent = new Intent(activity, EntryActivity.class);
            entryIntent.putExtra("type", entryType);
            entryIntent.putExtra("month", entryMonth);
            entryIntent.putExtra("entryId", entryId);
            entryIntent.putExtra("directory", directory);

            if (onUpdateListener != null) {
                entryIntent.putExtra("entry_position", holder.getAdapterPosition()); // new stuff
            }

            activity.startActivity(entryIntent);
            // activity.onBackPressed();
        });
    }
}
