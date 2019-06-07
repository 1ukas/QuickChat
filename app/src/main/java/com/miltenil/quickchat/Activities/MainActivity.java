package com.miltenil.quickchat.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.miltenil.quickchat.Adapters.RecyclerViewAdapter;
import com.miltenil.quickchat.Fragments.MenuFragment;
import com.miltenil.quickchat.R;
import com.miltenil.quickchat.Utils.AddFriend;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String TITLE_KEY = "Inbox";
    private static final String USERS_KEY = "users";
    private static final String DISPLAY_NAME_KEY = "displayname";
    private static final String AVATAR_KEY = "avatar";
    private static final String AVATARS_KEY = "avatars";
    private static final String RECEIVED_MESSAGES_KEY = "received-messages";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ArrayList<String> mFriendNames = new ArrayList<>();
    private ArrayList<String> mFriendImageUrls = new ArrayList<>();
    private ArrayList<String> mVideoUris = new ArrayList<>();
    private ArrayList<Boolean> mReadMessages = new ArrayList<>();
    private ArrayList<String> mDocumentIDs = new ArrayList<>();
    private ArrayList<String> mMessageDates = new ArrayList<>();

    private ArrayList<String> senderUids = new ArrayList<>();

    private void HandleEmptyInbox() {
        TextView emptyInboxMessage = findViewById(R.id.empty_inbox_message);
        emptyInboxMessage.setVisibility(View.VISIBLE);
    }

    private void GetUserAvatars() {
        CollectionReference avatars = db.collection(AVATARS_KEY);
        for (int i = 0; i <= senderUids.size() - 1; ++i) {
            final int index = i;
            avatars.document(senderUids.get(i)).get()
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
                            if (index == senderUids.size() - 1) {
                                initRecyclerView();
                            }
                        }
                    });
        }
    }

    private void GetMessages() {
        db = FirebaseFirestore.getInstance();
        String collectionPath = (USERS_KEY + "/" + mAuth.getUid() + "/" + RECEIVED_MESSAGES_KEY);
        final CollectionReference receivedMessages = db.collection(collectionPath);
        receivedMessages.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "onComplete: GetMessages: Got a Message.");

                                mVideoUris.add(document.getString("videoURI"));
                                mReadMessages.add(document.getBoolean("read"));
                                senderUids.add(document.getString("senderUID"));
                                mDocumentIDs.add(document.getId());
                                mMessageDates.add(document.getString("date"));
                            }
                        }
                        if (senderUids.size() >= 1 && !senderUids.get(0).isEmpty()) {
                            GetUserAvatars();
                        }
                        else {
                            HandleEmptyInbox();
                        }
                    }
                });
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recycler view.");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mFriendImageUrls, mFriendNames, mReadMessages, mVideoUris, mDocumentIDs, mMessageDates, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) { // If the user is not logged in, open LogInActivity
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
        }
        else if (currentUser.getDisplayName().equals("")) { // If the user has not set his display name, open DisplayNameActivity
            Intent intent = new Intent(this, DisplayNameActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.this.setTitle(TITLE_KEY);
        mAuth = FirebaseAuth.getInstance(); // Initialise Auth System

        // Menu Fragment:
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_menu_placeholder, new MenuFragment());
        ft.commit();

        GetMessages();
    }
}
