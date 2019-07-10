package com.team3000.logit;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
    // private ArrayList<Pair<Entry, String>> entries;
    private ArrayList<EntryPair> entries;
    private boolean firstLoad;
    private boolean activityRestarted;
    private boolean configChanged;

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
            /*
            Pair<Entry, String> oldEntryPair = entries.get(entryPosition);
            String entryId = oldEntryPair.second;

            entries.set(entryPosition, new Pair<>(updatedEntry, entryId));
            */

            EntryPair oldEntryPair = entries.get(entryPosition);
            String entryId = oldEntryPair.getEntryId();

            entries.set(entryPosition, new EntryPair(updatedEntry, entryId));
            logAdapter.notifyItemChanged(entryPosition);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "OnCreate");

        Bundle bundle = getArguments();
        this.collectionName = bundle.getString("collectionName");
        this.type = bundle.getString("logType");
        this.db = FirebaseFirestore.getInstance();
        this.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.directory = String.format(Locale.US, "users/%s/collections/%s/%s"
        , userID, collectionName, type);
        this.collectionReference = db.collection(directory);

        // Handle screen load part
        // must put here, regardless of savedInstanceState is null or not to prevent nullPointer exception
        this.entries = new ArrayList<>();
        if (savedInstanceState != null) {
            /*
            this.finishedLoading = savedInstanceState.getBoolean("finishedLoading", false);
            if (configChanged && finishedLoading) {
                this.entries = savedInstanceState.getParcelableArrayList("entryPairs");
            }
            */
            this.configChanged = savedInstanceState.getBoolean("configChanged", false);
            this.firstLoad = savedInstanceState.getBoolean("firstLoad", true);
            this.activityRestarted = savedInstanceState.getBoolean("activityRestarted", false);
            // Log.i(TAG, String.valueOf(configChanged));
            // Log.i(TAG, String.valueOf(firstLoad));
            if (configChanged && !firstLoad) {
                ArrayList<EntryPair> entryPairs = savedInstanceState.getParcelableArrayList("entryPairs");
                this.entries = (entryPairs == null) ? new ArrayList<>() : entryPairs;
                if (this.entries == null) Log.i(TAG, "entries is null");
            }
        } else {
            this.firstLoad = true;
        }

        this.logAdapter = new CollectionLogAdapter(getActivity(), entries)
                .setOnDestroyListener(new OnDestroyListener())
                .setOnUpdateListener(new OnUpdateListener());

        this.listenerRegistration = collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "listen:error", e);
                return;
            }
            Log.i(TAG, "in listener registration");

            if (firstLoad) {
                Log.i(TAG, "in first load");
                List<String> dbPaths = new ArrayList<>();
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) { // later need to add a boolean here to detect that it is not first load
                        dbPaths.add(dc.getDocument().getString("dataPath"));
                    }
                }
                fetchRespectiveEntries(dbPaths);
            } else if (!configChanged && !firstLoad){
                Log.i(TAG, "in adding new entry");
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) { // later need to add a boolean here to detect that it is not first load
                        addNewEnty(dc.getDocument().getString("dataPath"));
                    }
                }
            }

            Log.i(TAG, "activityRestarted " + String.valueOf(activityRestarted));
            if (configChanged && activityRestarted) configChanged = false;
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listenerRegistration.remove();
        Log.i(TAG, "onDestroy " + type);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.configChanged = true;
        this.activityRestarted = true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        // outState.putBoolean("finishedLoading", finishedLoading);
        outState.putBoolean("firstLoad", firstLoad);
        outState.putBoolean("configChanged", configChanged);
        outState.putBoolean("activityRestarted", activityRestarted);
        outState.putParcelableArrayList("entryPairs", entries);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView " + type);
    }

    private void loadEntriesData() {
        Log.i(TAG, "In loadEntriesData " + collectionName + " " + type);
        Log.i(TAG, collectionReference.getPath());
        collectionReference.get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                Log.i(TAG, "Successfully fetch all collection entries");
                QuerySnapshot result = task.getResult();
                List<QueryDocumentSnapshot> collection_entries = new LinkedList<>();

                for (QueryDocumentSnapshot doc : result) {
                    collection_entries.add(doc);
                }

                // fetchRespectiveEntries(collection_entries);
            } else {
                Log.e(TAG, "Fail to load collection entries!");
            }
        });
    }

    /*
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
                    if (doc != null) Log.i(TAG, doc.getId());
                    // For debugging purpose
                    // Log.i(TAG, entry.getTitle());
                    // Log.i(TAG, entry.getDesc());
                    // Log.i(TAG, entry.getDate());

                    // entries.add(new Pair<>(entry, entryID));

                    // For testing
                    entries.add(new EntryPair(entry, entryID));
                }
            }

            logAdapter.notifyDataSetChanged(); // new stuff here
            firstLoad = false;
            finishedLoading = true;
        }  ));
    }
    */

    // Add new entry into the entry list of collection log
    private void addNewEnty(String partialdbPath) {
        String dbPath = String.format(Locale.US, "users/%s/%s", userID, partialdbPath);
        db.document(dbPath).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i(TAG, "Adding new entry");
                DocumentSnapshot doc = task.getResult();
                Entry newEntry = doc.toObject(Entry.class);
                String entryID = doc.getId();

                // entries.add(new Pair<>(newEntry, entryID));
                entries.add(new EntryPair(newEntry, entryID));
                logAdapter.notifyItemInserted(entries.size() - 1);
            } else {
                Log.e(TAG, "Fail to add new entry!");
            }
        });
    }

    private void onFinishLoading() {
        logAdapter.notifyDataSetChanged(); // new stuff here
        firstLoad = false;
    }

    private void fetchRespectiveEntries(List<String> directories) {
        String partial_dbPath = String.format(Locale.US, "users/%s", userID);

        List<Task<DocumentSnapshot>> tasks = new LinkedList<>();

        // Spawn off fetching tasks
        for (String directory : directories) {
            String dbPath = String.format(Locale.US, "%s/%s",
                    partial_dbPath, directory);
            Log.i(TAG, dbPath);
            tasks.add(db.document(dbPath).get());
        }

        // When all fetching tasks complete
        Tasks.whenAllComplete(tasks).addOnCompleteListener((task -> {
            Log.i(TAG, "All fetch tasks completes");
            List<Task<?>> tasks_list = task.getResult();

            for (Task<?> task1 : tasks_list) {
                if (task1.isSuccessful()) {
                    DocumentSnapshot doc = (DocumentSnapshot) task1.getResult();
                    Entry entry = doc.toObject(Entry.class);
                    String entryID = doc.getId();
                    if (doc != null) Log.i(TAG, doc.getId());
                    // For debugging purpose
                    // Log.i(TAG, entry.getTitle());
                    // Log.i(TAG, entry.getDesc());
                    // Log.i(TAG, entry.getDate());

                    // entries.add(new Pair<>(entry, entryID));

                    // For testing
                    entries.add(new EntryPair(entry, entryID));
                }
            }

            logAdapter.notifyDataSetChanged(); // new stuff here
            firstLoad = false;
            // finishedLoading = true;
        }  ));
    }
}
