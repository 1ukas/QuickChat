package com.miltenil.quickchat.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
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
import com.miltenil.quickchat.Activities.ProfileActivity;
import com.miltenil.quickchat.Activities.VideoCaptureActivity;
import com.miltenil.quickchat.R;
import com.miltenil.quickchat.Utils.AddFriend;

public class MenuFragment extends Fragment {

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
        Intent intent = new Intent(getActivity(), VideoCaptureActivity.class);
        startActivity(intent);
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
}
