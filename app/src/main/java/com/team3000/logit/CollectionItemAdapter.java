package com.team3000.logit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CollectionItemAdapter extends FirestoreRecyclerAdapter<CollectionItem, CollectionItemAdapter.CollectionViewHolder> {
    private Context context;
    private OnItemClickListener listener;

    public static class CollectionViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            this.container = (LinearLayout) itemView;
        }

        public void bind(CollectionItem item, final OnItemClickListener listener) {
            TextView nameView = container.findViewById(R.id.collection_name);
            nameView.setText(item.getName());

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick();
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick();
    }

    public CollectionItemAdapter(@NonNull FirestoreRecyclerOptions<CollectionItem> options, Context context, OnItemClickListener listener) {
        super(options);
        this.context = context;
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
                .inflate(R.layout.collection_list_item, parent, false);

        return new CollectionViewHolder(container);
    }
}
