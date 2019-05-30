package com.miltenil.quickchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miltenil.quickchat.Fragments.MenuFragment;
import com.miltenil.quickchat.R;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final String TITLE_KEY = "Profile";
    private static final int MIN_PASSWORD_LENGTH = 6;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    Button changePassButton;
    EditText passwordField;
    EditText emailField;
    TextView successText;

    private String passString;

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) { // If the user is not logged in, open LogInActivity
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
        }
        else if (currentUser.getDisplayName().equals("")) {
            Intent intent = new Intent(this, DisplayNameActivity.class);
            startActivity(intent);
        }
    }

    View.OnClickListener passwordButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            passwordField.setVisibility(View.VISIBLE);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String passwordString = passwordField.getText().toString();
                    if (passwordString.length() < MIN_PASSWORD_LENGTH) {
                        Toast.makeText(ProfileActivity.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        ChangePassword(passwordString);
                    }
                }
            });
        }
    };

    View.OnClickListener reAuthButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String emailInput = emailField.getText().toString();
            final String passwordInput = passwordField.getText().toString();

            AuthCredential credential = EmailAuthProvider
                    .getCredential(emailInput, passwordInput);

            // Prompt the user to re-provide their sign-in credentials
            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User re-authenticated.");

                                currentUser.updatePassword(passString)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User password updated.");

                                                    passwordField.setVisibility(View.GONE);
                                                    emailField.setVisibility(View.GONE);
                                                    changePassButton.setVisibility(View.GONE);
                                                    successText.setText(R.string.pass_changed);
                                                }
                                            }
                                        });
                            }
                            else {
                                Toast.makeText(ProfileActivity.this, "Re-authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ProfileActivity.this.setTitle(TITLE_KEY);
        mAuth = FirebaseAuth.getInstance(); // Initialise Auth System

        // Menu Fragment:
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_menu_placeholder, new MenuFragment());
        ft.commit();

        passwordField = findViewById(R.id.profile_password_field);
        changePassButton = findViewById(R.id.profile_change_password_btn);
        changePassButton.setOnClickListener(passwordButtonListener);
    }

    private void ChangePassword (String passwordString) {
        passString = passwordString;
        emailField = findViewById(R.id.profile_email_field);
        successText = findViewById(R.id.profile_success);
        successText.setText(R.string.reauth_message);
        successText.setVisibility(View.VISIBLE);
        emailField.setVisibility(View.VISIBLE);
        passwordField.getText().clear();
        changePassButton.setText(R.string.reauth_profile_prompt);
        changePassButton.setOnClickListener(reAuthButtonListener);
    }
}
