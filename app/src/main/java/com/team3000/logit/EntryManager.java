package com.team3000.logit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Locale;

public class EntryManager {
    private static final String TAG = "EntryManager";
    private Activity activity;
    private FirebaseFirestore firestore;
    private FirebaseUser user;

    public EntryManager(Activity activity) {
        this.activity = activity;
        this.firestore = FirebaseFirestore.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
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

    public void deleteEntry(DocumentReference entryRef) {
        entryRef.get().addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                String collection_path = task.getResult().getString("collection_path");
                entryRef.delete().addOnCompleteListener((task2 -> {
                    if (task2.isSuccessful() && !collection_path.isEmpty()) {
                        deleteFromCollection(collection_path, (task3) -> {
                            if (task3.isSuccessful()) {
                                Log.i(TAG, "Succesfully deleted from collection!");
                            } else {
                                Log.i(TAG, "Fail to delete from collection!");
                            }
                            activity.finish();
                        });
                    }
                }));
            }
        }));
    }

    // Add a new entry into a collection(tag) if the collectionField is not empty
    public void addIntoCollection(final String COLLECTIONNAME, final String TYPE, final String DBPATH,
                                  final DocumentReference ENTRYREF) {
        // Entry data
        final HashMap<String, String> DATA = new HashMap<>();
        DATA.put("dataPath", DBPATH);

        // Listener used after the entry is added into a collection(tag)
        final OnCompleteListener<Void> LISTENER1 = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(activity, "Fail to add to collection!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Fail to add to collection!");
                }
            }
        };

        if (!COLLECTIONNAME.isEmpty()) {
            final DocumentReference DOCREFERENCE = firestore.document(
                    String.format("users/%s/collections/%s", user.getUid(), COLLECTIONNAME));

            DOCREFERENCE.set(new CollectionItem(COLLECTIONNAME)) // The collection(tag) will be created if it hasn't exist
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
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
                        }
                    });
        }
    }

    public void addIntoCollectionForExistingDoc(final String newCollection, String oldCollection, final String type,
                                                final String docPath, final DocumentReference entryRef,
                                                String curr_collection_path) {
        if (!oldCollection.isEmpty() && newCollection.isEmpty()) {
            Log.i(TAG, "In adding into collection for existing doc");
            deleteFromCollection(curr_collection_path, (task -> {
                if (task.isSuccessful()) {
                    Log.i(TAG, "Deleted from old collection!");
                }
            }));
        } else if (oldCollection.isEmpty() && !newCollection.isEmpty()) {
            addIntoCollection(newCollection, type, docPath, entryRef);

        } else if (!newCollection.equals(oldCollection)) {
            deleteFromCollection(curr_collection_path, (task -> {
                if (task.isSuccessful()) {
                    Log.i(TAG, "Deleted from old collection A!");
                    addIntoCollection(newCollection, type, docPath, entryRef);
                }
            }));
        }
    }

    public void deleteFromCollection(String partialdbPath, OnCompleteListener<Void> listener) {
        Log.i(TAG, "In deleting from collection");
        String dbPath = String.format("/users/%s/collections/%s", user.getUid(), partialdbPath);
        // Log.i(TAG, dbPath); used for debugging
        firestore.document(dbPath).delete().addOnCompleteListener(listener);
    }
}
