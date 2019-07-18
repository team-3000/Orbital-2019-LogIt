package com.team3000.logit;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class EntryHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    protected View mView;
    protected TextView tvListTitle;
    protected TextView tvListDate;
    protected TextView tvListTime;
    protected TextView tvListDesc;
    protected View selectedOverlay;
    private ClickListener listener;

    public EntryHolder(View itemView, ClickListener listener) {
        super(itemView);
        mView = itemView;
        tvListTitle = mView.findViewById(R.id.tvListTitle);
        tvListDate = mView.findViewById(R.id.tvListDate);
        tvListTime = mView.findViewById(R.id.tvListTime);
        tvListDesc = mView.findViewById(R.id.tvListDesc);
        selectedOverlay = mView.findViewById(R.id.selectedOverlay);
        this.listener = listener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onItemClicked(getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (listener != null) {
            return listener.onItemLongClicked(getAdapterPosition());
        }
        return false;
    }

    public interface ClickListener {
        void onItemClicked(int position);
        boolean onItemLongClicked(int position);
    }
}
