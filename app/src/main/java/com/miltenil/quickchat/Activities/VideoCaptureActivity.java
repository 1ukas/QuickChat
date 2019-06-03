package com.miltenil.quickchat.Activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miltenil.quickchat.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import static java.security.AccessController.getContext;

public class VideoCaptureActivity extends AppCompatActivity {

    private static final String TAG = "VideoCaptureActivity";
    private static final String TITLE_KEY = "Capture";
    private static final int VIDEO_CAPTURE = 101;
    private static final int MAX_VIDEO_LENGTH = 15;
    private static final int MULTIPLE_PERMISSION_REQUEST = 10;

    private FirebaseAuth mAuth;

    private boolean HasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSION_REQUEST: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    CaptureVideo();
                }
                else {
                    Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void CaptureVideo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MULTIPLE_PERMISSION_REQUEST);
        }
        else {
            if (!HasCamera()) {
                Toast.makeText(this, "Your Device Doesn't Have A Usable Camera", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (!isExternalStorageWritable()) {
                Toast.makeText(this, "Unable To Get External Storage", Toast.LENGTH_SHORT).show();
                return;
            }

            File path = new File (Environment.getExternalStorageDirectory(), "QuickChat");
            if (!path.exists()) {
                path.mkdirs();
            }

            File videoFile = new File(path, UUID.randomUUID().toString().substring(0,5) + ".mp4");
            Uri videoUri = FileProvider.getUriForFile(this, "com.miltenil.fileprovider", videoFile);

            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_LENGTH);
            startActivityForResult(intent, VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Video saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video", Toast.LENGTH_LONG).show();
            }
        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        this.setTitle(TITLE_KEY);
        mAuth = FirebaseAuth.getInstance(); // Initialise Auth System
        CaptureVideo();
    }
}
