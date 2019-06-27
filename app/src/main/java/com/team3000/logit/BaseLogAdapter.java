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

    public BaseLogAdapter(Activity activity) {
        this.activity = activity;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @Override
    public EntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new EntryHolder(mView);
    }

    public void fillUpEntryHolder(EntryHolder holder, Entry entry, String entryId) {
        Log.i(TAG, "In fillUpEntryHolder");
        holder.tvListTitle.setText(entry.getTitle());
        holder.tvListDate.setText(entry.getDate());
        holder.tvListTime.setText(entry.getTime());
        holder.tvListDesc.setText(entry.getDesc());
        // final DocumentSnapshot doc = getSnapshots().getSnapshot(holder.getAdapterPosition());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entryType = entry.getType();
                int entryYear = entry.getYear();
                String entryMonth = entry.getMonth();
                // String entryId = doc.getId();
                String directory = String.format(Locale.US, "users/%s/%s/%d/%s/%s", userId, entryType, entryYear, entryMonth, entryId);
                Intent intent = new Intent(activity, EntryActivity.class);
                intent.putExtra("month", entryMonth);
                intent.putExtra("type", entryType);
                intent.putExtra("entryId", entryId);
                intent.putExtra("directory", directory);
                activity.startActivity(intent);
            }
        });
    }
}
