package com.team3000.logit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
    private TextView resendVerificationLink;
    private TextView forgotPasswordLink;

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
        resendVerificationLink = findViewById(R.id.resendVerificationLink);
        forgotPasswordLink = findViewById(R.id.forgotPassWordLink);

        setClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Redirect user if the user has already signed in
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            updateUI();
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

        resendVerificationLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                ResendEmailFragment fragment = new ResendEmailFragment();
                fragment.show(getSupportFragmentManager(), "dialog");
            }
        });

        forgotPasswordLink.setOnClickListener(v -> {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            PasswordResetFragment fragment = new PasswordResetFragment();
            fragment.show(getSupportFragmentManager(), "dialog");
        });

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
                                    updateUI();
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

    private void updateUI() {
        // Extra is put so that when the user clicks on today in drawer they won't be redirected to
        // the same page again after they signed in and landed in the today's daily log page
        Intent intentToday = new Intent(LoginActivity.this, DailyLogActivity.class)
                .putExtra("year", 0);
        startActivity(intentToday);
        finish(); // destroy this activity
    }
}
