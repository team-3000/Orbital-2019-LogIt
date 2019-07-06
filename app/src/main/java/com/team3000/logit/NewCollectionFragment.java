package com.team3000.logit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class NewCollectionFragment extends DialogFragment {
    private View dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialog = inflater.inflate(R.layout.new_collection_dialog, container, false);
        setClickListeners();

        return dialog;
    }

    // Set the size of the dialog window
    @Override
    public void onStart() {
        super.onStart();

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;

        Window window = getDialog().getWindow();
        window.setLayout(width, height);
    }

    private void setClickListeners() {
        final EditText nameField = dialog.findViewById(R.id.name_field);
        Button submitButton = dialog.findViewById(R.id.submit_btn);

        submitButton.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String collectionName = nameField.getText().toString().trim();

            if (!collectionName.isEmpty()) {
                HashMap<String, String> data = new HashMap<>();
                data.put("name", collectionName);

                String dbPath = String.format("users/%s/collections/", user.getUid());
                CollectionReference collections = FirebaseFirestore.getInstance().collection(dbPath);

                // Add the new collection tag
                collections.document(collectionName).set(data)
                        .addOnCompleteListener(task -> {
                            DialogFragment dialog = NewCollectionFragment.this;
                            String message;

                            if (task.isSuccessful()) {
                                message = "Collection created";
                            } else {
                                message = "Fail to create collection";
                            }

                            dialog.dismiss();
                            Toast.makeText(dialog.getContext(), message, Toast.LENGTH_SHORT)
                                    .show();
                        });
            } else {
                nameField.setError("Required");
            }
        });
    }
}
