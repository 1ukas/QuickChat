package com.miltenil.quickchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.miltenil.quickchat.R;

import java.util.HashMap;
import java.util.Map;

public class AddFriendActivity extends AppCompatActivity {

    private static final String TAG = "AddFriendActivity";
    private static final String USERS_KEY = "users";
    private static final String DISPLAY_NAME_KEY = "displayname";
    private static final String DISPLAY_NAME_LOWER_KEY = "displaynamelower";
    private static final String AVATAR_KEY = "avatar";
    private static final String EMAIL_LOWER_KEY = "emaillower";
    private static final String FRIENDS_KEY = "friends";
    private static final String AT_KEY = "@";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    public void CreateFriend(String friendUid, String friendDisplayName, String friendDisplayNameLower, String friendAvatarUrl) {
        String collectionPath = (USERS_KEY + "/" + mAuth.getUid() + "/" + FRIENDS_KEY);
        Map<String, Object> data = new HashMap<>();
        data.put(DISPLAY_NAME_KEY, friendDisplayName);
        data.put(DISPLAY_NAME_LOWER_KEY, friendDisplayNameLower);
        data.put(AVATAR_KEY, friendAvatarUrl);
        //new CreateInDatabase(db, collectionPath, friendUid, data, false);
    }

    public void FindFriend(String friendDetails) {
        db = FirebaseFirestore.getInstance();
        CollectionReference users = db.collection(USERS_KEY);

        if (friendDetails.toLowerCase().contains(AT_KEY)) {
            users.whereEqualTo(EMAIL_LOWER_KEY, friendDetails.toLowerCase()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    Log.d(TAG, "Unable To Find User by Email.");
                                }
                                else {
                                    Log.d(TAG, "onComplete: FindFriend: Found by Email.");

                                    final String friendUid = task.getResult().getDocuments().get(0).getId();
                                    final String friendDisplayName = task.getResult().getDocuments().get(0).getString(DISPLAY_NAME_KEY);
                                    final String friendDisplayNameLower = task.getResult().getDocuments().get(0).getString(DISPLAY_NAME_LOWER_KEY);
                                    final String friendAvatarUrl = task.getResult().getDocuments().get(0).getString(AVATAR_KEY);
                                    CreateFriend(friendUid, friendDisplayName, friendDisplayNameLower, friendAvatarUrl);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else {
            users.whereEqualTo(DISPLAY_NAME_LOWER_KEY, friendDetails.toLowerCase()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    Log.d(TAG, "Unable To Find User By Display Name.");
                                }
                                else {
                                    Log.d(TAG, "onComplete: FindFriend: Found by Display Name.");

                                    final String friendUid = task.getResult().getDocuments().get(0).getId();
                                    final String friendDisplayName = task.getResult().getDocuments().get(0).getString(DISPLAY_NAME_KEY);
                                    final String friendDisplayNameLower = task.getResult().getDocuments().get(0).getString(DISPLAY_NAME_LOWER_KEY);
                                    final String friendAvatarUrl = task.getResult().getDocuments().get(0).getString(AVATAR_KEY);
                                    CreateFriend(friendUid, friendDisplayName, friendDisplayNameLower, friendAvatarUrl);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        mAuth = FirebaseAuth.getInstance(); // Initialise Auth System

        final Button addFriendBtn = findViewById(R.id.add_friend_btn);
        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText addFriendInput = findViewById(R.id.add_friend_field);
                final String addFriendString = addFriendInput.getText().toString();
                if (!addFriendString.isEmpty()){
                    FindFriend(addFriendString);
                }
                else {
                    Toast.makeText(AddFriendActivity.this, "Invalid User Details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_post_menu:
                Toast.makeText(this, "New Post", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.add_friend_menu:
                Intent intent = new Intent(this, AddFriendActivity.class);
                startActivity(intent);
                //Toast.makeText(MainActivity.this, "Add Friend", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout_menu:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
