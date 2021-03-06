package com.team3000.logit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CollectionItemAdapter extends FirestoreRecyclerAdapter<CollectionItem, CollectionItemAdapter.CollectionViewHolder> {
    private OnItemClickListener listener;

    public static class CollectionViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView container;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            this.container = (AppCompatTextView) itemView;
        }

        public void bind(CollectionItem item, final OnItemClickListener listener) {
            container.setText(item.getName());
            container.setOnClickListener(v -> listener.onItemClick(container));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    public CollectionItemAdapter(@NonNull FirestoreRecyclerOptions<CollectionItem> options, OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull CollectionViewHolder viewHolder, int i, @NonNull CollectionItem collectionItem) {
        viewHolder.bind(collectionItem, this.listener);
    }
    
    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View container = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);

        return new CollectionViewHolder(container);
    }
}
