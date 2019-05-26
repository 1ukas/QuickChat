package com.miltenil.quickchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class FriendsListActivity extends AppCompatActivity {
    private static final String TAG = "FriendsListActivity";
    private FirebaseAuth mAuth;

    private ArrayList<String> mFriendNames = new ArrayList<>();
    private ArrayList<String> mFriendImageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendslist);
        Log.d(TAG, "onCreate: started");

        mAuth = FirebaseAuth.getInstance(); // Initialise Auth System
        initImages();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
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
