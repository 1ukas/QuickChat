package com.miltenil.quickchat.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.miltenil.quickchat.Activities.FriendsListActivity;
import com.miltenil.quickchat.Activities.LogInActivity;
import com.miltenil.quickchat.Activities.MainActivity;
import com.miltenil.quickchat.Activities.ProfileActivity;
import com.miltenil.quickchat.R;
import com.miltenil.quickchat.Utils.AddFriend;

import java.io.File;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MenuFragment extends Fragment {

    private static final int VIDEO_CAPTURE = 101;
    private static final int MULTIPLE_PERMISSION_REQUEST = 10;
    private static final int MAX_VIDEO_LENGTH = 15;
    private static final int MAX_VIDEO_QUALITY = 1;
    private static final String DIRECTORY_NAME_KEY = "QuickChat";
    private static final String AUTHORITY_KEY = "com.miltenil.fileprovider";
    private static final String VIDEO_FORMAT_KEY = ".mp4";

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAuth = FirebaseAuth.getInstance(); // Initialise Auth System
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_menu, parent, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getActivity(), "Video saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Video recording cancelled.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Failed to record video", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSION_REQUEST: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    CaptureVideo();
                }
                else {
                    Toast.makeText(getActivity(), "Permissions Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_items, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_post_menu:
                HandleNewPostItem();
                return true;
            case R.id.add_friend_menu:
                HandleAddFriendItem();
                return true;
            case R.id.friends_list_menu:
                HandleFriendsListItem();
                return true;
            case R.id.profile_menu:
                HandleProfileItem();
                return true;
            case R.id.logout_menu:
                HandleLogoutItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void HandleNewPostItem() {
        CaptureVideo();
    }

    private void HandleAddFriendItem() {
        final EditText addFriendField = getView().findViewById(R.id.addFriendNameInput);
        final Button addFriendButton = getView().findViewById(R.id.addFriendNameBtn);
        addFriendField.setVisibility(View.VISIBLE);
        addFriendButton.setVisibility(View.VISIBLE);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String addFriendString = addFriendField.getText().toString();
                if (!addFriendString.isEmpty()) {
                    new AddFriend(FirebaseFirestore.getInstance()).FindFriend(addFriendString, mAuth.getUid());
                    Toast.makeText(getActivity(), "Friend Added Successfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Invalid Details", Toast.LENGTH_SHORT).show();
                }
                addFriendField.setVisibility(View.GONE);
                addFriendButton.setVisibility(View.GONE);
            }
        });
    }

    private void HandleFriendsListItem() {
        Intent intent = new Intent(getActivity(), FriendsListActivity.class);
        startActivity(intent);
    }

    private void HandleProfileItem() {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);
    }

    private void HandleLogoutItem() {
        FirebaseAuth.getInstance().signOut(); // Log user out
        Intent intent = new Intent(getActivity(), LogInActivity.class);
        startActivity(intent); // Open LogInActivity after logging user out
    }

    private boolean HasCamera() {
        return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void CaptureVideo() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MULTIPLE_PERMISSION_REQUEST);
        }
        else {
            if (!HasCamera()) {
                Toast.makeText(getActivity(), "Your Device Doesn't Have A Usable Camera", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (!isExternalStorageWritable()) {
                Toast.makeText(getActivity(), "Unable To Get External Storage", Toast.LENGTH_SHORT).show();
                return;
            }

            File path = new File (Environment.getExternalStorageDirectory(), DIRECTORY_NAME_KEY);
            if (!path.exists()) {
                path.mkdirs();
            }

            File videoFile = new File(path, UUID.randomUUID().toString().substring(0,5) + VIDEO_FORMAT_KEY);
            Uri videoUri = FileProvider.getUriForFile(getActivity(), AUTHORITY_KEY, videoFile);

            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_LENGTH);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, MAX_VIDEO_QUALITY);
            //intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 5491520L); // 5mb limit
            startActivityForResult(intent, VIDEO_CAPTURE);
        }
    }
}
