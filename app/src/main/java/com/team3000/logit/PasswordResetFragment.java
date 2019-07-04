package com.team3000.logit;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetFragment extends DialogFragment {
    private FirebaseAuth mAuth;
    private EditText fieldEmail;
    private Button buttonReset;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout parent = (LinearLayout) inflater.inflate(R.layout.fragment_reset_password, container, false);
        fieldEmail = parent.findViewById(R.id.field_email);
        buttonReset = parent.findViewById(R.id.button_reset);
        setClickListeners();

        return parent;
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
        buttonReset.setOnClickListener(v -> {
            sendPassWordResetEmail();
        });
    }

    private void sendPassWordResetEmail() {
        String email = fieldEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            fieldEmail.setError("Required!");
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Password reset email is sent, please check your email", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Fail to send password reset email!", Toast.LENGTH_SHORT).show();
            }

            this.dismiss();
        });
    }
}
