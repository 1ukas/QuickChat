package com.miltenil.quickchat.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.miltenil.quickchat.Fragments.MenuFragment;
import com.miltenil.quickchat.R;
import com.miltenil.quickchat.Adapters.RecyclerViewAdapter;

import java.util.ArrayList;

public class FriendsListActivity extends AppCompatActivity {

    private static final String TAG = "FriendsListActivity";
    private static final String TITLE_KEY = "Friends";
    private static final String USERS_KEY = "users";
    private static final String FRIENDS_KEY = "friends";
    private static final String DISPLAY_NAME_KEY = "displayname";
    private static final String AVATAR_KEY = "avatar";
    private static final String AVATARS_KEY = "avatars";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private boolean sendMessage;
    private Uri videoUri;

    private ArrayList<String> mFriendNames = new ArrayList<>();
    private ArrayList<String> mFriendImageUrls = new ArrayList<>();

    private ArrayList<String> friendUids = new ArrayList<>();

    private void HandleEmptyFriendsList() {
        TextView emptyInboxMessage = findViewById(R.id.empty_friendslist_message);
        emptyInboxMessage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendslist);
        FriendsListActivity.this.setTitle(TITLE_KEY);
        Log.d(TAG, "onCreate: started");

        mAuth = FirebaseAuth.getInstance(); // Initialise Auth System

        sendMessage = getIntent().getBooleanExtra("sendMessage", false);
        videoUri = getIntent().getParcelableExtra("videoUri");

        // Menu Fragment:
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_menu_placeholder, new MenuFragment());
        ft.commit();

        GetFriends();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) { // If the user is not logged in, open LogInActivity
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
        }
        else if (currentUser.getDisplayName().equals("")) {
            Intent intent = new Intent(this, DisplayNameActivity.class);
            startActivity(intent);
        }
    }

    private void GetUserAvatars() {
        CollectionReference avatars = db.collection(AVATARS_KEY);
        for (int i = 0; i <= friendUids.size() - 1; ++i) {
            final int index = i;
            avatars.document(friendUids.get(i)).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: GetFriends: Found a User Avatar.");

                                mFriendImageUrls.add(task.getResult().getString(AVATAR_KEY));
                                mFriendNames.add(task.getResult().getString(DISPLAY_NAME_KEY));
                            }
                            else {
                                Log.d(TAG, "Error Getting Documents: ", task.getException());
                            }
                            if (index == friendUids.size() - 1) {
                                initRecyclerView();
                            }
                        }
                    });
        }
    }

    public void GetFriends() {
        db = FirebaseFirestore.getInstance();
        String collectionPath = (USERS_KEY + "/" + mAuth.getUid() + "/" + FRIENDS_KEY);
        final CollectionReference friends = db.collection(collectionPath);
        friends.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "onComplete: GetFriends: Found a Friend.");

                                friendUids.add(document.getId());
                            }
                        } else {
                            Log.d(TAG, "Error Getting Documents: ", task.getException());
                        }
                        if (friendUids.size() >= 1 && !friendUids.get(0).isEmpty()) {
                            GetUserAvatars();
                        }
                        else {
                            HandleEmptyFriendsList();
                        }
                    }
                });
    }
    
    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recycler view.");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter;
        if (sendMessage) {
            adapter = new RecyclerViewAdapter(mFriendImageUrls, mFriendNames, mAuth.getUid(), videoUri, FriendsListActivity.this);
        }
        else {
            adapter = new RecyclerViewAdapter(mFriendImageUrls, mFriendNames, FriendsListActivity.this);
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(FriendsListActivity.this));
    }
}
