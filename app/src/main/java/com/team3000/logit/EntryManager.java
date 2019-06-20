package com.team3000.logit;

import android.content.Context;
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
    private Context context;
    private String Tag;
    private FirebaseFirestore firestore;
    private FirebaseUser user;

    public EntryManager(Context context, String Tag) {
        this.context = context;
        this.Tag = Tag;
        this.firestore = FirebaseFirestore.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
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
                    Log.e(Tag, "Fail to add to collection!");
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
                            String collection_entry_path = String.format(Locale.US, "%s/%s/%s",
                                    COLLECTIONNAME, TYPE, ref.getId());
                            HashMap<String, String > add_on = new HashMap<>();
                            add_on.put("collection_entry_path", collection_entry_path);
                            ENTRYREF.set(add_on, SetOptions.merge());
                        }
                    });
        }
    }

    public void addIntoCollectionForExistingDoc(String newCollection, String type, String dbPath, DocumentReference entryRef) {
        if (newCollection.isEmpty()) {

        } else {

        }
    }

    public void deleteFromCollection(String id) {
        String dbPath = String.format("users/%s/collections");

    }
}
