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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class CollectionLogFragment extends Fragment {
    private static final String TAG = "CollectionLogFragment";
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private String collectionName;
    private String type;
    private String directory;
    private String userID;
    private List<Pair<Entry, String>> entries;
    private CollectionLogAdapter logAdapter;
    private EntryListener.OnDestroyListener onDestroyListener;

    public class OnDestroyListener implements EntryListener.OnDestroyListener {
        @Override
        public void onDestroy(int entryPosition) {
            Log.i(TAG, "In OnDestroyListener");
            entries.remove(entryPosition);
            logAdapter.notifyItemRemoved(entryPosition);
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

        // create the adapter with an empty list of entries data first, once the data is completely loaded
        // call notifyDataSetChanged on the adapter (as shown in fetchRespectiveEntries()
        this.onDestroyListener = new OnDestroyListener();
        this.logAdapter = new CollectionLogAdapter(getActivity(), entries, onDestroyListener);

        loadEntriesData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout parentView = (LinearLayout) inflater.inflate(R.layout.fragment_entry_list,
                container, false);

        recyclerView = parentView.findViewById(R.id.rvLogRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(logAdapter);

        return parentView;
    }

    private void loadEntriesData() {
        Log.i(TAG, "In loadEntriesData");
        db.collection(directory).get().addOnCompleteListener((task) -> {
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
                    Log.i(TAG, entry.getTitle());
                    // Log.i(TAG, entry.getDesc());
                    // Log.i(TAG, entry.getDate());

                    entries.add(new Pair<>(entry, entryID));
                }
            }

            logAdapter.notifyDataSetChanged(); // new stuff here
        }  ));
    }
}
