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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

// Represents a page(note, task or entry) in the Collection Log.
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
    private ArrayList<EntryPair> entriesPairs;
    private boolean firstTimeLoading;
    private boolean startObservingNewEntry;
    private boolean configChanged;

    public class OnUpdateListener implements  EntryListener.OnUpdateListener {
        @Override
        public void onUpdate(int entryPosition, Entry updatedEntry) {
            Log.i(TAG, "In OnUpdateListener");

            EntryPair oldEntryPair = entriesPairs.get(entryPosition);
            String entryId = oldEntryPair.getEntryId();

            // Use an iterator to traverse the list to find the correct entryPair and
            // update it to the new entryPair
            ListIterator<EntryPair> iterator = entriesPairs.listIterator();
            int size = entriesPairs.size();
            for (int i = 0; i < size; i++) {
                if (iterator.nextIndex() == entryPosition) {
                    iterator.next();
                    iterator.set(new EntryPair(updatedEntry, entryId));
                    logAdapter.notifyItemChanged(entryPosition);
                }
            }
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

        handleDisplayOfEntries(savedInstanceState);
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
        Log.i(TAG, "onConfigChanged " + type);
        this.configChanged = true;
    }

    // Pass the loaded entry data to this activity upon restarting (e.g. when configuration changes)
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        outState.putBoolean("firstTimeLoading", firstTimeLoading);
        outState.putBoolean("configChanged", configChanged);
        outState.putParcelableArrayList("entryPairs", entriesPairs);
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

    // Handle the display of entries data in the recyclerview. It includes logic that deals with
    // adding, reading, updating & deleting of entry.
    private void handleDisplayOfEntries(Bundle savedInstanceState) {
        this.entriesPairs = new ArrayList<>();

        // Logic to handle orientation change.
        if (savedInstanceState != null) {
            this.configChanged = savedInstanceState.getBoolean("configChanged", false);
            this.firstTimeLoading = savedInstanceState.getBoolean("firstTimeLoading", true);

            // If data is fully loaded, then when orientation changes, the previously loaded data will
            // be used for the adapter instead of fetching all the data from the database again.
            if (configChanged && !firstTimeLoading) {
                ArrayList<EntryPair> entryPairs = savedInstanceState.getParcelableArrayList("entryPairs");
                this.entriesPairs = (entryPairs == null) ? new ArrayList<>() : entryPairs;
            }
        } else {
            // First time loading data, so fetch from database
            this.firstTimeLoading = true;
        }

        this.logAdapter = new CollectionLogAdapter(getActivity(), entriesPairs)
                .setOnUpdateListener(new OnUpdateListener());

        // Logic to load all existing entry data from database. Also loads new entry data from database.
        this.listenerRegistration = collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "listen:error", e);
                return;
            }
            Log.i(TAG, "in listener registration " + type);

            // Fetch data from database if this is the first time loading all the data from database
            // The second if statement deals with event where new entry is added into the database
            if (firstTimeLoading) {
                Log.i(TAG, "in first load " + type);
                List<String> dbPaths = new ArrayList<>();
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        dbPaths.add(dc.getDocument().getString("dataPath"));
                    }
                }
                fetchRespectiveEntries(dbPaths);
            } else  if (startObservingNewEntry) {
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.i(TAG, "in adding new entry " + type);
                            addNewEnty(dc.getDocument().getString("dataPath"));
                            break;
                        case REMOVED:
                            Log.i(TAG, "in removing new entry " + type);
                            String entryID = dc.getDocument().getString("dataPath")
                                    .split("/")[3];
                            removeEntry(entryID);
                            break;
                        default:
                    }
                }
            }

            /*
                Used to prevent the second if statement being triggered on orientation changes, which will
                fetch all the data from database again and hence result in duplicated data.
                It is disabled(i.e setting it to TRUE) after the listenerRegistration is triggered for the 1st time
                to enable the logic that deals with adding & deleting of entry
            */
            startObservingNewEntry = true;
        });
    }

    // Fetch entries data from database and use it to load the recyclerview
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

        // When all fetching tasks complete, fill them up in the adapter's data set(list) and notify it
        // that the data set is changed.
        Tasks.whenAllComplete(tasks).addOnCompleteListener((task -> {
            Log.i(TAG, "All fetch tasks completes");
            List<Task<?>> tasks_list = task.getResult();

            for (Task<?> task1 : tasks_list) {
                if (task1.isSuccessful()) {
                    DocumentSnapshot doc = (DocumentSnapshot) task1.getResult();
                    Entry entry = doc.toObject(Entry.class);
                    String entryID = doc.getId();

                    entriesPairs.add(new EntryPair(entry, entryID));
                }
            }

            logAdapter.notifyDataSetChanged();

            // Use to prevent unnecessary fetching of data from the database, esp when orientation
            // changes. It is set to false(to prevent unnecessary fetc
            firstTimeLoading = false;
        }  ));
    }

    // Add new entry into the entry list of collection log (Deal with displaying of data, not the database)
    private void addNewEnty(String partialdbPath) {
        String dbPath = String.format(Locale.US, "users/%s/%s", userID, partialdbPath);
        db.document(dbPath).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i(TAG, "Adding new entry");
                DocumentSnapshot doc = task.getResult();
                Entry newEntry = doc.toObject(Entry.class);
                String entryID = doc.getId();

                entriesPairs.add(new EntryPair(newEntry, entryID));
                logAdapter.notifyItemInserted(entriesPairs.size() - 1);
            } else {
                Log.e(TAG, "Fail to add new entry into Collection Log's display!");
            }
        });
    }

    // Delete an entry from the entry list of collection log (Deal with displaying of data, not the database)
    private void removeEntry(String entryID) {
        int size  = entriesPairs.size();
        ListIterator<EntryPair> iterator = entriesPairs.listIterator();

        // Find the entry data from the adapter's data set using entry id, then delete it
        // & finally notify the adapter that item is deleted.
        for (int index = 0; index < size; index++) {
            if (entryID.equals(iterator.next().getEntryId())) {
                iterator.remove();
                logAdapter.notifyItemRemoved(index);
                Log.i(TAG, "Nofified item removed");
                break;
            }
        }
    }
}