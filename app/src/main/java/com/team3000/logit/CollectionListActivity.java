package com.team3000.logit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CollectionListActivity extends BaseActivity {
    private FirestoreRecyclerAdapter<CollectionItem, CollectionItemAdapter.CollectionViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.currPosition = R.id.nav_collections;

        FrameLayout contentFrame = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_collection_list, contentFrame);

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

        adapter = new CollectionItemAdapter(options, (view -> {
            String title = ((TextView) view).getText().toString();

            Intent i = new Intent(CollectionListActivity.this, CollectionActivity.class)
                    .putExtra("collection_name", title);
            startActivity(i);
            // activityStack.push(CollectionListActivity.this);
        }));

        RecyclerView list = findViewById(R.id.collections_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                layoutManager.getOrientation());

        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);
        list.addItemDecoration(dividerItemDecoration);
    }
}
