package com.team3000.logit;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class EntryListActivity extends BaseActivity implements EntryHolder.ClickListener {
    private String TAG = "EntryListActivity";
    private FloatingActionButton fabAddEntryList;
    private String type;
    private String directory;
    private ArrayList<Entry> entries = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EntryListAdapter mAdapter;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

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
        mAdapter = new EntryListAdapter(EntryListActivity.this, entries, this);
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

    @Override
    protected void onStop() {
        super.onStop();
        if (actionMode != null) {
            actionMode.finish();
        }
        getSupportActionBar().show();
    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null) {
            actionMode = startActionMode(actionModeCallback);
        }
        toggleSelection(position);
        return true;
    }

    /**
     * Toggle the selection state of an item.
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
            if (getSupportActionBar().isShowing()) {
                getSupportActionBar().hide();
            }
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.multi_select_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.barMultiDelete:
                    // TODO: delete items
                    AlertDialog.Builder builder = new AlertDialog.Builder(EntryListActivity.this);
                    builder.setMessage("Delete " + mAdapter.getSelectedItemCount() + " items?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    EntryManager entryManager = new EntryManager(EntryListActivity.this);
                                    String actualPathPartial = String.format("users/%s", user);
                                    List<Integer> selectedItems = mAdapter.getSelectedItems();
                                    for (int i : selectedItems) {
                                        Entry currEntry = entries.get(i);
                                        String actualPath = String.format(Locale.US, "%s/%s/%d/%s", actualPathPartial,
                                                currEntry.getType(), currEntry.getYear(), currEntry.getMonth());
                                        DocumentReference ref = db.document(actualPath + "/" + currEntry.getId());
                                        entryManager.deleteFromTracker(currEntry.getType() + "Store", actualPath);
                                        ref.get().addOnCompleteListener(task -> {
                                            String eisen = task.getResult().getString("eisen");
                                            if (!"".equals(eisen)) {
                                                entryManager.deleteFromTracker(eisen, directory);
                                            }
                                        });
                                        // The EntryActivity will straightaway close once item is deleted in Firestore (handled in deleteEntry())
                                        entryManager.deleteEntry(ref);
                                        startActivity(new Intent(EntryListActivity.this, EntryListActivity.class));
                                        mode.finish();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelection();
            actionMode = null;
            getSupportActionBar().show();
        }
    }
}
