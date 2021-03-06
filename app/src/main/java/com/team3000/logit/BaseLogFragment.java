package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Locale;

public class BaseLogFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String logType = getArguments().getString("logType");
        String directory = getArguments().getString("directory");

        Query query;
        if ("daily".equals(logType)) {
            query = db.collection(directory)
                    .whereEqualTo("date", getArguments().getString("logDate"))
//                    Composite queries requires manual indexing via Firestore console, no programmatic way to set index
                    .orderBy("time");
            initialiseRecyclerView(view, query);
        } else {
            query = db.collection(directory)
                    .whereEqualTo("monthlyLog", true)
                    .orderBy("date")
                    .orderBy("time");
            initialiseRecyclerView(view, query);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void initialiseRecyclerView(View view, Query query)   {
        FirestoreRecyclerOptions<Entry> options = new FirestoreRecyclerOptions.Builder<Entry>()
                .setQuery(query, Entry.class)
                .build();

        mAdapter = new FirestoreRecyclerAdapter<Entry, EntryHolder>(options) {
            @Override
            public void onBindViewHolder(final EntryHolder holder, int position, final Entry entry) {
                holder.tvListTitle.setText(entry.getTitle());
                holder.tvListDate.setText(entry.getDate());
                holder.tvListTime.setText(entry.getTime());
                holder.tvListDesc.setText(entry.getDesc());
                final DocumentSnapshot doc = getSnapshots().getSnapshot(holder.getAdapterPosition());
                holder.mView.setOnClickListener(v -> {
                    String entryType = entry.getType();
                    int entryYear = entry.getYear();
                    String entryMonth = entry.getMonth();
                    String entryId = doc.getId();
                    String directory = String.format(Locale.US, "users/%s/%s/%d/%s/%s",
                            FirebaseAuth.getInstance().getCurrentUser().getUid(), entryType, entryYear, entryMonth, entryId);
                    Intent intent = new Intent(getContext(), EntryActivity.class);
                    intent.putExtra("type", entryType);
                    intent.putExtra("month", entryMonth);
                    intent.putExtra("entryId", entryId);
                    intent.putExtra("directory", directory);
                    startActivity(intent);
                });
            }

            @Override
            public EntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);
                return new EntryHolder(mView);
            }
        };
        RecyclerView recyclerView = view.findViewById(R.id.rvLogRV);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }
}

