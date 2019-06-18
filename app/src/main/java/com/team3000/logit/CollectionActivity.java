package com.team3000.logit;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CollectionActivity extends BaseActivity {
    FirestoreRecyclerAdapter<CollectionItem, CollectionItemAdapter.CollectionViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrame = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_collection, contentFrame);

        getSupportActionBar().setTitle("Collections");
        setUpRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    // Set up the recycler view and attach its adapter to the adapter field of this
    // activity
    private void setUpRecyclerView() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String dbpath = String.format("users/%s/collections/", user.getUid());

        Query query = FirebaseFirestore.getInstance().collection(dbpath);
        FirestoreRecyclerOptions<CollectionItem> options = new FirestoreRecyclerOptions.Builder<CollectionItem>()
                .setQuery(query, CollectionItem.class)
                .build();

        adapter = new CollectionItemAdapter(options, this, new CollectionItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick() {
                Toast.makeText(CollectionActivity.this, "This is clicked!", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        RecyclerView list = findViewById(R.id.collections_list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }
}
