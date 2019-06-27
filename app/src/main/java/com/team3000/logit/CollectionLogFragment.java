package com.team3000.logit;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class CollectionLogFragment extends Fragment {
    private static final String TAG = "CollectionLogFragment";
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private ListenerRegistration listenerRegistration;
    private BaseLogAdapter logAdapter;
    private RecyclerView recyclerView;
    private String collectionName;
    private String type;
    private String directory;
    private String userID;
    private List<Pair<Entry, String>> entries;
    private Boolean firstLoad;

    public class OnDestroyListener implements EntryListener.OnDestroyListener {
        @Override
        public void onDestroy(int entryPosition) {
            Log.i(TAG, "In OnDestroyListener");
            entries.remove(entryPosition);
            logAdapter.notifyItemRemoved(entryPosition);
        }
    }

    public class OnUpdateListener implements  EntryListener.OnUpdateListener {
        @Override
        public void onUpdate(int entryPosition, Entry updatedEntry) {
            Log.i(TAG, "In OnUpdateListener");

            // Try to use iterator later so that we won't have to tranverse the list twice
            Pair<Entry, String> oldEntryPair = entries.get(entryPosition);
            String entryId = oldEntryPair.second;

            entries.set(entryPosition, new Pair<>(updatedEntry, entryId));
            logAdapter.notifyItemChanged(entryPosition);
        }
    }

    // For system's use
    public CollectionLogFragment() {}

    public CollectionLogFragment(String collectionName, String type) {
        this.collectionName = collectionName;
        this.type = type;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.entries = new LinkedList<>();
        this.db = FirebaseFirestore.getInstance();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.directory = String.format(Locale.US, "users/%s/collections/%s/%s"
        , userID, collectionName, type);
        collectionReference = db.collection(directory);

        // create the adapter with an empty list of entries data first, once the data is completely loaded
        // call notifyDataSetChanged on the adapter (as shown in fetchRespectiveEntries()
        // this.onDestroyListener =
        this.logAdapter = new CollectionLogAdapter(getActivity(), entries)
                .setOnDestroyListener(new OnDestroyListener())
                .setOnUpdateListener(new OnUpdateListener());

        firstLoad = true;
        listenerRegistration = collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                if (!firstLoad) {
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Log.i(TAG, "onEvent");
                                addNewEnty(dc.getDocument().getString("dataPath"));
                                break;
                            default:
                        }
                    }
                }
            }
        });

        loadEntriesData();
    }

    /*
    // Attach event listener to listen to event where new collection entry is added
    @Override
    public void onStart() {
        super.onStart();
        listenerRegistration = collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.i(TAG, "onEvent");
                            addNewEnty(dc.getDocument().getString("dataPath"));
                            break;
                    }
                }
            }
        });
    }
    */

    @Override
    public void onDestroy() {
        super.onDestroy();
        listenerRegistration.remove();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout parentView = (LinearLayout) inflater.inflate(R.layout.fragment_log_list,
                container, false);

        recyclerView = parentView.findViewById(R.id.rvLogRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(logAdapter);

        return parentView;
    }

    private void loadEntriesData() {
        Log.i(TAG, "In loadEntriesData");
        collectionReference.get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                Log.i(TAG, "Successfully fetch all collection entries");
                QuerySnapshot result = task.getResult();
                List<QueryDocumentSnapshot> collection_entries = new LinkedList<>();

                for (QueryDocumentSnapshot doc : result) {
                    collection_entries.add(doc);
                }

                fetchRespectiveEntries(collection_entries);
            } else {
                Log.e(TAG, "Fail to load collection entries!");
            }
        });
    }

    private void fetchRespectiveEntries(List<QueryDocumentSnapshot> collection_entries) {
        Log.i(TAG, "In fetchRespectiveEntries");

        String partial_dbPath = String.format(Locale.US, "users/%s", userID);

        List<Task<DocumentSnapshot>> tasks = new LinkedList<>();
        for (QueryDocumentSnapshot collection_entry : collection_entries) {
            String dbPath = String.format(Locale.US, "%s/%s",
                    partial_dbPath, collection_entry.getString("dataPath"));
            Log.i(TAG, dbPath);
            tasks.add(db.document(dbPath).get());
        }

        Tasks.whenAllComplete(tasks).addOnCompleteListener((task -> {
            Log.i(TAG, "All fetch tasks completes");
            List<Task<?>> tasks_list = task.getResult();

            for (Task<?> task1 : tasks_list) {
                if (task1.isSuccessful()) {
                    DocumentSnapshot doc = (DocumentSnapshot) task1.getResult();
                    Entry entry = doc.toObject(Entry.class);
                    String entryID = doc.getId();

                    // For debugging purpose
                    // Log.i(TAG, entry.getTitle());
                    // Log.i(TAG, entry.getDesc());
                    // Log.i(TAG, entry.getDate());

                    entries.add(new Pair<>(entry, entryID));
                }
            }

            logAdapter.notifyDataSetChanged(); // new stuff here
            firstLoad = false;
        }  ));
    }

    // Add new entry into the entry list of collection log
    private void addNewEnty(String partialdbPath) {
        String dbPath = String.format(Locale.US, "users/%s/%s", userID, partialdbPath);
        db.document(dbPath).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i(TAG, "Adding new entry");
                DocumentSnapshot doc = task.getResult();
                Entry newEntry = doc.toObject(Entry.class);
                String entryID = doc.getId();

                entries.add(new Pair<>(newEntry, entryID));
                logAdapter.notifyItemInserted(entries.size() - 1);
            } else {
                Log.e(TAG, "Fail to add new entry!");
            }
        });
    }
}
