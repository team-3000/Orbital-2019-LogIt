package com.team3000.logit;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResendEmailFragment extends DialogFragment {
    FirebaseAuth mAuth;
    TextView mEmailField;
    TextView mPasswordField;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    // Initialise and return the dialog view of this fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View dialog = inflater.inflate(R.layout.resend_verification_dialog, container, false);

        // Set clickListener for the dialog
        final Button resendButton = dialog.findViewById(R.id.resendVerification_button);
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailField = dialog.findViewById(R.id.emailField);
                mPasswordField = dialog.findViewById(R.id.passwordField);

                String email = mEmailField.getText().toString().trim();
                String password = mPasswordField.getText().toString();

                logInAndResend(email, password);
            }
        });

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

    // Log the user in, resend the verification email
    // & then sign the user out
    private void logInAndResend(String email, String password) {
        if (validateForm(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                resendEmailVerification();
                            } else {
                                Toast.makeText(getContext(), "Email and/or password is incorrect",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Resend verification email and then sign the user out
    private void resendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String message = "";
                if (task.isSuccessful()) {
                    mAuth.signOut();
                    ResendEmailFragment.this.dismiss();
                    Toast.makeText(getContext(), "Successfully resend verification email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Fail to resend verification email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Check if the filled-in form is valid
    private boolean validateForm(String email, String password) {
        boolean isValid = true;

        // Check all the fields
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            isValid = false;
        }

        return isValid;
    }
}
