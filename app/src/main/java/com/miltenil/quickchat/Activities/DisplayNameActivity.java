package com.miltenil.quickchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.miltenil.quickchat.Database.DatabaseController;
import com.miltenil.quickchat.R;

import java.util.HashMap;
import java.util.Map;

public class DisplayNameActivity extends AppCompatActivity {

    private static final String TAG = "DisplayNameActivity";
    private static final String DISPLAY_NAME_KEY = "displayname";
    private static final String DISPLAY_NAME_LOWER_KEY = "displaynamelower";
    private static final String USERS_KEY = "users";
    private static final String AVATARS_KEY = "avatars";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public void UpdateDisplayName (String displayName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DatabaseController databaseController = new DatabaseController(db);
        String uid = currentUser.getUid();

        //Update Display Name in Database:
        Map<String, Object> data = new HashMap<>();
        data.put(DISPLAY_NAME_KEY, displayName);
        data.put(DISPLAY_NAME_LOWER_KEY, displayName.toLowerCase());
        databaseController.UpdateInDatabase(USERS_KEY, uid, data);
        //new UpdateInDatabase(db, USERS_KEY, uid, data);

        //Update Display Name in Avatar Database:
        Map<String, Object> data_avatar = new HashMap<>();
        data_avatar.put(DISPLAY_NAME_KEY, displayName);

        databaseController.UpdateInDatabase(AVATARS_KEY, uid, data_avatar);
        //new UpdateInDatabase(db, AVATARS_KEY, uid, data_avatar);

        //Update Display Name in Firebase Auth System:
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();
        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Display Name Updated in Firebase Auth.");
                            Intent intent = new Intent(DisplayNameActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayname);
        mAuth = FirebaseAuth.getInstance(); // Initialise Auth System

        final Button displayNameButton = findViewById(R.id.displayNameBtn);
        displayNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText displayNameInput = findViewById(R.id.displayNameText);
                final String displayNameString = displayNameInput.getText().toString();
                if (!displayNameString.isEmpty()){
                    UpdateDisplayName(displayNameString);
                }
                else {
                    Toast.makeText(DisplayNameActivity.this, "Invalid Display Name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) { // If the user is not logged in, open LogInActivity
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
        }
    }
}
