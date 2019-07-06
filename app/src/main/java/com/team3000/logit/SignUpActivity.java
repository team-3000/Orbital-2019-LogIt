package com.team3000.logit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;
    private EditText mUserNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        // Find all necessary views
        mUserNameField = findViewById(R.id.nameField);
        mEmailField = findViewById(R.id.emailField);
        mPasswordField = findViewById(R.id.passwordField);
        mConfirmPasswordField = findViewById(R.id.confirmPasswordField);
        signUpButton = findViewById(R.id.signUp_button);

        setClickListeners();
    }

    private void setClickListeners() {
        signUpButton.setOnClickListener(v -> signUp());
    }

    private void signUp() {
        final String username = mUserNameField.getText().toString().trim();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        String confirmPassword = mConfirmPasswordField.getText().toString();

        // Sign up the user only if the signUp form is valid
        // If the sign-up task is successful, send verification email to the user
        if (validateForm(username, email, password, confirmPassword)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        // Sign up success, set user's name and send verification email
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            intialiseProfile(user, username);
                            sendEmailVerification(user);
                        } else {
                            // If sign up fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Sign up failed :( Please try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /**
     * Validate the signUp form
     *
     * @return a boolean indicating whether the signUp form is valid
     */
    private boolean validateForm(String username, String email, String password, String confirmPassword) {
        boolean isValid = true;

        // Check the validity of all the fields
        if (TextUtils.isEmpty(username)) {
            mUserNameField.setError("Required");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            isValid = false;
        } else if (password.length() < 8) {
            Toast.makeText(this, "password must be at least 8 characters long!", Toast.LENGTH_SHORT).show();
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

    // Initialize the user's profile (Currently only the user's name)
    private void intialiseProfile(FirebaseUser user, String username) {
        // Construct a UserProfileChangeRequest object
        UserProfileChangeRequest profileInitialization =
                new UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build();

        user.updateProfile(profileInitialization)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Username is initialised");
                    } else {
                        Log.e(TAG, "Fail to initialize username!");
                    }
                });
    }

    private void sendEmailVerification(final FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
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
                });
    }
}
