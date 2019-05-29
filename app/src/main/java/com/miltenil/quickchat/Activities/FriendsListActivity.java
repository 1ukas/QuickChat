package com.miltenil.quickchat.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
    private static final String USERS_KEY = "users";
    private static final String FRIENDS_KEY = "friends";
    private static final String DISPLAY_NAME_KEY = "displayname";
    private static final String AVATAR_KEY = "avatar";
    private static final String AVATARS_KEY = "avatars";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private ArrayList<String> mFriendNames = new ArrayList<>();
    private ArrayList<String> mFriendImageUrls = new ArrayList<>();

    private ArrayList<String> friendUids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendslist);
        FriendsListActivity.this.setTitle("Friends");
        Log.d(TAG, "onCreate: started");

        mAuth = FirebaseAuth.getInstance(); // Initialise Auth System

        // Menu Fragment:
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_menu_placeholder, new MenuFragment());
        ft.commit();

        GetFriends();
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
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
                });
    }

    private void initImages() {
        Log.d(TAG, "initImages: preparing images.");

        mFriendImageUrls.add("https://cdn1.iconfinder.com/data/icons/user-pictures/101/malecostume-512.png");
        mFriendNames.add("This Dude");

        mFriendImageUrls.add("https://cdn1.iconfinder.com/data/icons/user-pictures/100/male3-512.png");
        mFriendNames.add("Wadu Hek");

        mFriendImageUrls.add("https://cdn1.iconfinder.com/data/icons/user-pictures/100/female1-512.png");
        mFriendNames.add("Hello There");

        mFriendImageUrls.add("https://cdn1.iconfinder.com/data/icons/user-pictures/100/boy-512.png");
        mFriendNames.add("General Kenobi");

        mFriendImageUrls.add("https://cdn1.iconfinder.com/data/icons/user-pictures/100/supportmale-512.png");
        mFriendNames.add("Waow");

        mFriendImageUrls.add("https://cdn0.iconfinder.com/data/icons/user-pictures/100/matureman1-512.png");
        mFriendNames.add("Are You a Boo");

        mFriendImageUrls.add("https://cdn1.iconfinder.com/data/icons/user-pictures/100/girl-512.png");
        mFriendNames.add("Ted Lopez");

        mFriendImageUrls.add("https://cdn0.iconfinder.com/data/icons/user-pictures/100/unknown_1-2-512.png");
        mFriendNames.add("Noo u");

        initRecyclerView();
    }
    
    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recycler view.");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mFriendImageUrls, mFriendNames, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
