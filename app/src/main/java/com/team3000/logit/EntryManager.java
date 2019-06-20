package com.team3000.logit;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Locale;

public class EntryManager {
    private Context context;
    private String tag;
    private FirebaseFirestore firestore;
    private FirebaseUser user;

    public EntryManager(Context context, String Tag) {
        this.context = context;
        this.tag = Tag;
        this.firestore = FirebaseFirestore.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }

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
                    Toast.makeText(context, "Fail to add to collection!", Toast.LENGTH_SHORT).show();
                    Log.e(tag, "Fail to add to collection!");
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
                            // Add the entry into the collection(tag)
                            DocumentReference ref = DOCREFERENCE.collection(String.format(Locale.US, "%s", TYPE))
                                    .document();
                            ref.set(DATA).addOnCompleteListener(LISTENER1);

                            // Attach the id in collection to the existing entry
                            String collection_path = String.format(Locale.US, "%s/%s/%s",
                                    COLLECTIONNAME, TYPE, ref.getId());
                            HashMap<String, String > add_on = new HashMap<>();
                            add_on.put("collection_path", collection_path);
                            ENTRYREF.set(add_on, SetOptions.merge());
                        }
                    });
        }
    }

    public void addIntoCollectionForExistingDoc(String newCollection, String oldCollection, String type,
                                                String docPath, DocumentReference entryRef,
                                                String curr_collection_path) {
        if (newCollection.isEmpty()) {
            Log.e(tag, "In adding into collection for existing doc");
            deleteFromCollection(curr_collection_path, null);
        }

        /*
        if (newCollection.isEmpty()) {
            Log.e(tag, "In adding into collection for existing doc");
            Log.e(tag, entryRef.getPath());
            entryRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String collection_entry_path = (String) task.getResult().get("collection_entry_path");
                        Log.e(tag, collection_entry_path);
                        deleteFromCollection(collection_entry_path, null);
                    }
                }
            });
        } else {
            // Complete the logic here
        }
        */
    }

    public void deleteFromCollection(String partialdbPath, OnCompleteListener<Void> listener) {
        Log.e(tag, "In deleting from collection");
        String dbPath = String.format("/users/%s/collections/%s",user.getUid(), partialdbPath);
        firestore.document(dbPath).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(tag, "Successfully delete from collection");
            }
        });
    }
}
