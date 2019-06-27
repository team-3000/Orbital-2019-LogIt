package com.team3000.logit;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public abstract class BaseLogAdapter extends RecyclerView.Adapter<EntryHolder> {
    private static final String TAG = "BaseLogAdapter";
    private Activity activity;
    private String userId;
    protected EntryListener.OnDestroyListener onDestroyListener;
    protected EntryListener.OnUpdateListener onUpdateListener;

    public BaseLogAdapter(Activity activity) {
        this.activity = activity;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public BaseLogAdapter setOnDestroyListener(EntryListener.OnDestroyListener onDestroyListener) {
        this.onDestroyListener = onDestroyListener;
        return this;
    }

    public BaseLogAdapter setOnUpdateListener(EntryListener.OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
        return this;
    }

    @Override
    public EntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new EntryHolder(mView);
    }

    public void fillUpEntryHolder(EntryHolder holder, Entry entry, String entryId) {
        Log.i(TAG, "In fillUpEntryHolder");
        Log.i(TAG, entryId);
        holder.tvListTitle.setText(entry.getTitle());
        holder.tvListDate.setText(entry.getDate());
        holder.tvListTime.setText(entry.getTime());
        holder.tvListDesc.setText(entry.getDesc());
        // final DocumentSnapshot doc = getSnapshots().getSnapshot(holder.getAdapterPosition());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Attaching onDestroyListener");
                EntryManager.setOnDestroyListener(onDestroyListener); // For collection log
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

                if (onDestroyListener != null && onUpdateListener != null) {
                    entryIntent.putExtra("entry_position", holder.getAdapterPosition()); // new stuff
                }

                activity.startActivity(entryIntent);
                // activity.onBackPressed();
            }
        });
    }
}
