package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

public class EntryListActivity extends BaseActivity {
    private String TAG = "EntryListActivity";
    private FloatingActionButton fabAddEntryList;
    String type;
    String directory;
    private ArrayList<Entry> entries = new ArrayList<>();
    private ArrayList<String> entryRefs;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_entry_list, contentFrameLayout);
        fabAddEntryList = findViewById(R.id.fabAddEntryList);
        type = getIntent().getStringExtra("trackType");
        directory = String.format("users/%s", user.getUid());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter = new EntryListAdapter(EntryListActivity.this, entries);
        db.document(directory).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                entries.clear();
                entryRefs = (ArrayList<String>) task.getResult().get(type);
                for (String ref : entryRefs) {
                    db.document(ref).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();
                            Entry currEntry = doc.toObject(Entry.class);
                            currEntry.setId(doc.getId());
                            entries.add(currEntry);
                            Collections.sort(entries);
                            mAdapter.notifyDataSetChanged();
                            Log.d(TAG, currEntry.getId());
                        }
                    });
                }
            }
        });

        RecyclerView recyclerView = findViewById(R.id.rvEntryList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EntryListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        fabAddEntryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EntryListActivity.this, EntryFormActivity.class);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
    }
}
