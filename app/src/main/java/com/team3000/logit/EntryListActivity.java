package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntryListActivity extends BaseActivity {
    private String TAG = "EntryListActivity";
    private FloatingActionButton fabAddEntryList;
    private String type;
    private String directory;
    private ArrayList<Entry> entries = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_entry_list, contentFrameLayout);
        fabAddEntryList = findViewById(R.id.fabAddEntryList);
        type = getIntent().getStringExtra("trackType");
        directory = String.format("users/%s/%s", user.getUid(), type);
        if ("noteStore".equals(type) || "taskStore".equals(type) || "eventStore".equals(type)) {
            String heading = type.substring(0, 1).toUpperCase() + type.substring(1).replace("Store", "") + "s";
            getSupportActionBar().setTitle(heading);
        } else {
            String heading = type.substring(0, 1).toUpperCase() + type.substring(1);
            getSupportActionBar().setTitle(heading);
            fabAddEntryList.hide();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        entries.clear();
        mAdapter = new EntryListAdapter(EntryListActivity.this, entries);
        db.collection(directory).get().addOnCompleteListener(task -> {
            QuerySnapshot querySnapshot = task.getResult();
            if (querySnapshot != null) {
                List<DocumentSnapshot> storeSnaps = querySnapshot.getDocuments();
                for (DocumentSnapshot store : storeSnaps) {
                    Log.i(TAG, store.getId());
                    DocumentReference doc = db.document(store.getString("entryPath"));

                    // To handle cases where the delete button is pressed which straightaway closes EntryActivity
                    // but the entry in e.g. delegate node is not deleted yet, thus causing nullpointerexception
                    // This issue is mainly due to speed latency issue when deleting an entry across multiple nodes, e.g.
                    // from the main node, then collection node, and finally delegate node
                    if (doc != null) {
                        doc.get().addOnCompleteListener(task1 -> {
                            Entry currEntry = task1.getResult().toObject(Entry.class);

                            // Same reason as above comment
                            if (currEntry != null) {
                                currEntry.setId(doc.getId());
                                entries.add(currEntry);
                                Collections.sort(entries);
                                mAdapter.notifyDataSetChanged();
                                Log.d(TAG, currEntry.getId());
                            }
                        });
                    }
                }
            }
        });

        RecyclerView recyclerView = findViewById(R.id.rvEntryList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EntryListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        fabAddEntryList.setOnClickListener(v -> {
            Intent intent = new Intent(EntryListActivity.this, EntryFormActivity.class);
            intent.putExtra("type", type.replace("Store", ""));
            intent.putExtra("redirect", "entrylist");
            startActivity(intent);
            finish();
        });
    }
}
