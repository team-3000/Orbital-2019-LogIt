package com.team3000.logit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchListFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Entry> entries = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String searchQuery = getArguments().getString("searchQuery");
        String type = getArguments().getString("type") + "Store";
        String directory = String.format("users/%s/%s", FirebaseAuth.getInstance().getCurrentUser().getUid(), type);
        entries.clear();
        mAdapter = new EntryListAdapter(getActivity(), entries);
        db.collection(directory).get().addOnCompleteListener(task -> {
            QuerySnapshot querySnapshot = task.getResult();
            if (querySnapshot != null) {
                List<DocumentSnapshot> storeSnaps = querySnapshot.getDocuments();
                for (DocumentSnapshot store : storeSnaps) {
                    DocumentReference doc = db.document(store.getString("entryPath"));

                    // To handle cases where the delete button is pressed which straightaway closes EntryActivity
                    // but the entry in e.g. delegate node is not deleted yet, thus causing nullpointerexception
                    // This issue is mainly due to speed latency issue when deleting an entry across multiple nodes, e.g.
                    // from the main node, then collection node, and finally delegate node
                    if (doc != null) {
                        doc.get().addOnCompleteListener(task1 -> {
                            DocumentSnapshot result = task1.getResult();
                            if (((String) result.get("title")).toLowerCase().contains(searchQuery.toLowerCase()) ||
                                    ((String) result.get("desc")).toLowerCase().contains(searchQuery.toLowerCase())) {
                                Entry currEntry = result.toObject(Entry.class);

                                // Same reason as above comment
                                if (currEntry != null) {
                                    currEntry.setId(doc.getId());
                                    entries.add(currEntry);
                                    Collections.sort(entries);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.rvLogRV);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }
}
