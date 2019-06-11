package com.team3000.logit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button loginButton;
    private TextView signUpLink;
    // private TextView resendVerificationLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Find all the necessary fields
        mEmailField = findViewById(R.id.emailField);
        mPasswordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.login_button);
        signUpLink = findViewById(R.id.signUpLink);
        // resendVerificationLink = findViewById(R.id.resendVerificationLink);

        setClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Redirect user if the user has already signed in
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            Intent i = new Intent(this, DailyLogActivity.class);
            startActivity(i);
        }
    }

    private void setClickListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        /*
        resendVerificationLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = createResendDialog();
                dialog.show();
            }
        });
        */
    }

    private void logIn() {
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString();

        if (validateForm(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful() && mAuth.getCurrentUser().isEmailVerified()) {
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                // Only update the UI if user has verified their account
                                // through email
                                if (user.isEmailVerified()) {
                                    updateUI(user);
                                } else {
                                    mAuth.signOut();
                                    Toast.makeText(LoginActivity.this, "" +
                                                    "Either email or password entered is incorrect" +
                                                    "or have not performed email verification",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
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

    private void updateUI(FirebaseUser user) {
        Toast.makeText(this, "Username is " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
    }

    /*
    // Try using fragment instead to make code cleaner
    private AlertDialog createResendDialog() {
        // Create an alert dialog builder
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View dialog = getLayoutInflater().inflate(R.layout.resend_verification_dialog, null);

        // Set clickListener for the dialog
        final Button resendButton = dialog.findViewById(R.id.resendVerification_button);
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView emailField = dialog.findViewById(R.id.emailField);
                String email = emailField.getText().toString().trim();

                resendEmailVerification(email);
            }
        });

        // Attach the view to the dialog
        dialogBuilder.setView(dialog);

        // Create and return the dialog
        return dialogBuilder.create();
    }
    */

    /*
    private void resendEmailVerification(String email) {
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString();

        if (validateForm(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, makeResendEmailOnCompleteListeners());
        }
    }
    */

    /*
    // Try using fragment instead to make code cleaner
    private OnCompleteListener<AuthResult> makeResendEmailOnCompleteListeners() {
        OnCompleteListener<AuthResult> signInListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    sendEmailThenSignOUt();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            // Send verification email to user once they successfully sign in
            // then sign the user out again
            public void sendEmailThenSignOUt() {
                final FirebaseUser user = mAuth.getCurrentUser();
                user.sendEmailVerification()
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this,
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
            }};
    }
    */
}
