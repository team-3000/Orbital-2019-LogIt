package com.team3000.logit;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class EntryHolder extends RecyclerView.ViewHolder {
    View mView;
    TextView tvListTitle;
    TextView tvListDate;
    TextView tvListTime;
    TextView tvListDesc;

    public EntryHolder(View itemView) {
        super(itemView);
        mView = itemView;
        tvListTitle = mView.findViewById(R.id.tvListTitle);
        tvListDate = mView.findViewById(R.id.tvListDate);
        tvListTime = mView.findViewById(R.id.tvListTime);
        tvListDesc = mView.findViewById(R.id.tvListDesc);
    }
}
