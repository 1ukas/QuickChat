package com.miltenil.quickchat.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.miltenil.quickchat.Utils.CreateUser;
import com.miltenil.quickchat.R;

public class LogInActivity extends AppCompatActivity {

    public enum CheckInType {LogIn, SignUp}

    private static final String TAG = "LogInActivity";
    private static final int MIN_PASSWORD_LENGTH = 6;

    private FirebaseAuth mAuth;

    public void OpenMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void CheckInUser(CheckInType type) {
        final EditText emailInput = findViewById(R.id.emailField);
        final EditText passwordInput = findViewById(R.id.passwordField);
        final String emailString = emailInput.getText().toString();
        final String passwordString = passwordInput.getText().toString();

        if (emailString.isEmpty()) {
            Toast.makeText(LogInActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (passwordString.length() < MIN_PASSWORD_LENGTH) {
            Toast.makeText(LogInActivity.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (type) {
            case LogIn:
                mAuth.signInWithEmailAndPassword(emailString, passwordString)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            OpenMainActivity();
                        }
                        else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
                break;

            case SignUp:
                mAuth.createUserWithEmailAndPassword(emailString, passwordString)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            new CreateUser().AddNewUser(user.getUid(), user.getEmail());
                            Intent intent = new Intent(LogInActivity.this, DisplayNameActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "SignUp Failed :(", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance(); // Initialise Auth System

        Button loginButton = findViewById(R.id.loginBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckInUser(CheckInType.LogIn);
            }
        });
        Button signUpButton = findViewById(R.id.signupBtn);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckInUser(CheckInType.SignUp);
            }
        });
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }
}
