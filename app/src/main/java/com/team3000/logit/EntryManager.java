package com.team3000.logit;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EntryManager {
    private static final String TAG = "EntryManager";
    private static EntryListener.OnUpdateListener onUpdateListener;
    private Activity activity;
    private FirebaseFirestore firestore;
    private FirebaseUser user;

    public EntryManager(Activity activity) {
        this.activity = activity;
        this.firestore = FirebaseFirestore.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public static void setOnUpdateListener(EntryListener.OnUpdateListener listener) {
        Log.i(TAG, "In attaching onUpdate Listener");
        onUpdateListener = listener;
    }

    /*
    public void updateEntry(DocumentReference doc, HashMap<String, String> newData) {
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String old_collection_path = (String) task.getResult().get("collection_path");


                }
            }
        });
    }
    */

    public EntryManager deleteEntry(DocumentReference entryRef, int entryPosition) {
        entryRef.get().addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                Log.i(TAG, "Entry deleted!");

                String collection_path = task.getResult().getString("collection_path");
                entryRef.delete().addOnCompleteListener((task2 -> {
                    if (task2.isSuccessful() && !collection_path.isEmpty()) {
                        deleteFromCollection(collection_path, (task3) -> {
                            if (task3.isSuccessful()) {
                                Log.i(TAG, "Succesfully deleted from collection!");
                            } else {
                                Log.i(TAG, "Fail to delete from collection!");
                            }
                            // activity.startActivity(new Intent(activity, DailyLogActivity.class));
                            // activity.finish(); (move to EntryActivity delete button's setOnClickListener)
                        });
                    }
                }));
                activity.finish();
            }
        }));

        return this;
    }

    // Add a new entry into a collection(tag) if the collectionField is not empty
    public void addIntoCollection(final String COLLECTIONNAME, final String TYPE, final String DBPATH,
                                  final DocumentReference ENTRYREF) {
        // Entry data
        final HashMap<String, String> DATA = new HashMap<>();
        DATA.put("dataPath", DBPATH);

        // Listener used after the entry is added into a collection(tag)
        final OnCompleteListener<Void> LISTENER1 = task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(activity, "Fail to add to collection!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Fail to add to collection!");
            }
        };

        if (!COLLECTIONNAME.isEmpty()) {
            final DocumentReference DOCREFERENCE = firestore.document(
                    String.format("users/%s/collections/%s", user.getUid(), COLLECTIONNAME));

            DOCREFERENCE.set(new CollectionItem(COLLECTIONNAME)) // The collection(tag) will be created if it hasn't exist
                    .addOnCompleteListener(task -> {
                        Log.i(TAG, "Added into new collection!");
                        // Add the entry into the collection(tag)
                        DocumentReference ref = DOCREFERENCE.collection(String.format(Locale.US, "%s", TYPE))
                                .document();
                        ref.set(DATA).addOnCompleteListener(LISTENER1);

                        // Attach the id in collection to the existing entry
                        String collection_path = String.format(Locale.US, "%s/%s/%s",
                                COLLECTIONNAME, TYPE, ref.getId());
                        HashMap<String, String> add_on = new HashMap<>();
                        add_on.put("collection_path", collection_path);
                        ENTRYREF.set(add_on, SetOptions.merge());
                    });
        }
    }

    public void addIntoCollectionForExistingDoc(final String newCollection, String oldCollection, final String type,
                                                final String docPath, final DocumentReference entryRef,
                                                String curr_collection_path, int entryPosition) {
        Log.i(TAG, entryPosition + "");
        if (!oldCollection.isEmpty() && newCollection.isEmpty()) {
            Log.i(TAG, "In adding into collection for existing doc");
            deleteFromCollection(curr_collection_path, (task -> {
                if (task.isSuccessful()) {
                    Log.i(TAG, "Deleted from old collection!");
                }
            }));
            activity.finish();
        } else if (oldCollection.isEmpty() && !newCollection.isEmpty()) {
            addIntoCollection(newCollection, type, docPath, entryRef);

        } else if (!newCollection.equals(oldCollection)) {
            deleteFromCollection(curr_collection_path, (task -> {
                if (task.isSuccessful()) {
                    Log.i(TAG, "A Deleted from old collection!");
                    addIntoCollection(newCollection, type, docPath, entryRef);
                }
            }));
        } else if ((!oldCollection.isEmpty() && !newCollection.isEmpty()) && newCollection.equals(oldCollection)
            && entryPosition != -1){ // for collection log purpose
            entryRef.get().addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   Log.i(TAG, "Collection Log onUpdate");
                   Entry entry = task.getResult().toObject(Entry.class);
                   onUpdateListener.onUpdate(entryPosition, entry);
               }
            });
        }
    }

    public void deleteFromCollection(String partialdbPath, OnCompleteListener<Void> listener) {
        Log.i(TAG, "In deleting from collection");
        String dbPath = String.format("/users/%s/collections/%s", user.getUid(), partialdbPath);
        // Log.i(TAG, dbPath); used for debugging
        firestore.document(dbPath).delete().addOnCompleteListener(listener);
    }
  
    protected void updateTracker(String trackType, String updateDir) {
        if (!"".equals(trackType)) {
            String trackerPath = String.format("users/%s/%s", user.getUid(), trackType);
            Map<String, String> pathData = new HashMap<>();
            pathData.put("entryPath", updateDir);
            firestore.collection(trackerPath).add(pathData);
        }
    }

    protected void deleteFromTracker(String trackType, String entryDir) {
        if (!"".equals(trackType)) {
            String trackerPath = String.format("users/%s/%s", user.getUid(), trackType);
            firestore.collection(trackerPath).whereEqualTo("entryPath", entryDir)
                    .get().addOnCompleteListener(task -> {
                        List<DocumentSnapshot> ds = task.getResult().getDocuments();
                        if (!ds.isEmpty()) {
                            String trackerId = ds.get(0).getId();
                            firestore.document(trackerPath + "/" + trackerId).delete();
                        }
                    });
        }
    }

    protected void redirectToPrecedingPage(String redirect, String type) {
        Intent intentNew = new Intent();
        switch (redirect) {
            case "dailylog":
                intentNew = new Intent(activity, DailyLogActivity.class);
                break;
            case "monthlylog":
                intentNew = new Intent(activity, MonthlyLogActivity.class);
                break;
            case "entrylist":
                intentNew = new Intent(activity, EntryListActivity.class);
                intentNew.putExtra("trackType", type + "Store");
                break;
            default:
                break;
        }
        activity.startActivity(intentNew);
    }
}
