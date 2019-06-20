package com.team3000.logit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> {
    private List<Entry> entries;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvListTitle;
        private TextView tvListDate;
        private TextView tvListTime;
        private TextView tvListDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            tvListTitle = itemView.findViewById(R.id.tvListTitle);
            tvListDate = itemView.findViewById(R.id.tvListDate);
            tvListTime = itemView.findViewById(R.id.tvListTime);
            tvListDesc = itemView.findViewById(R.id.tvListDesc);
        }
    }

    public EntryAdapter(List<Entry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public EntryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvListTitle.setText(entries.get(position).getTitle());
        holder.tvListDate.setText(entries.get(position).getDate());
        holder.tvListTime.setText(entries.get(position).getTime());
        holder.tvListDesc.setText(entries.get(position).getDesc());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }
}
