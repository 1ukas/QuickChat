package com.miltenil.quickchat.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.miltenil.quickchat.Database.DatabaseController;
import com.miltenil.quickchat.R;

import java.util.HashMap;
import java.util.Map;

public class VideoPlayActivity extends AppCompatActivity {

    private static final String TAG = "VideoPlayActivity";
    private static final String TITLE_KEY = "Player";
    private static final String USERS_KEY = "users";
    private static final String RECEIVED_MESSAGES_KEY = "received-messages";
    private static final String READ_KEY = "read";
    private static final String DOCUMENT_ID_KEY = "documentID";
    private static final String VIDEO_URI_KEY = "videoUri";

    private FirebaseAuth mAuth;

    private void SetMessageAsRead() {
        DatabaseController databaseController = new DatabaseController(FirebaseFirestore.getInstance());
        String collectionPath = (USERS_KEY + "/" + mAuth.getUid() + "/" + RECEIVED_MESSAGES_KEY);
        String documentID = getIntent().getStringExtra(DOCUMENT_ID_KEY);
        Map<String, Object> data = new HashMap<>();
        data.put(READ_KEY, true);
        databaseController.UpdateInDatabase(collectionPath, documentID, data);
    }

    private void PlayVideo(String videoUri) {
        final VideoView videoView = findViewById(R.id.main_videoview);
        MediaController mc = new MediaController(this);
        videoView.setMediaController(mc);
        videoView.setVideoPath(videoUri);
        videoView.start();

        boolean read = getIntent().getBooleanExtra(READ_KEY, false);
        if (!read) {
            SetMessageAsRead();
        }

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(VideoPlayActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
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
        setContentView(R.layout.activity_videoplay);
        VideoPlayActivity.this.setTitle(TITLE_KEY);
        mAuth = FirebaseAuth.getInstance(); // Initialise Auth System

        String videoUri = getIntent().getStringExtra(VIDEO_URI_KEY);
        PlayVideo(videoUri);
    }
}
