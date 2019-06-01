package com.team3000.logit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    FirebaseAuth mAuth;
    EditText mEmailField;
    EditText mPasswordField;
    EditText mConfirmPasswordField;
    Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        // Find all necessary views
        mEmailField = findViewById(R.id.emailField);
        mPasswordField = findViewById(R.id.passwordField);
        mConfirmPasswordField = findViewById(R.id.confirmPasswordField);
        signUpButton = findViewById(R.id.signUp_button);

        setClickListeners();
    }

    private void setClickListeners() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        String confirmPassword = mConfirmPasswordField.getText().toString();

        // Sign up the user only if the signUp form is valid
        // If the sign-up task is successful, send verification email to the user
        if (validateForm(email, password, confirmPassword)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign up success, send verification email
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                sendEmailVerification(user);
                            } else {
                                // If sign up fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Sign up failed :( Please try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Validate the signUp form
     * @return a boolean indicating whether the signUp form is valid
     */
    private boolean validateForm(String email, String password, String confirmPassword) {
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

        if (TextUtils.isEmpty(confirmPassword)) {
            mConfirmPasswordField.setError("Required");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "password and confirmed password does not match"
                    , Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void sendEmailVerification(final FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(SignUpActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
