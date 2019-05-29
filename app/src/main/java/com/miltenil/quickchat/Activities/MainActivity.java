package com.miltenil.quickchat.Activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.miltenil.quickchat.Fragments.MenuFragment;
import com.miltenil.quickchat.R;
import com.miltenil.quickchat.Utils.AddFriend;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) { // If the user is not logged in, open LogInActivity
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
        }
        //updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.this.setTitle("Inbox");
        mAuth = FirebaseAuth.getInstance(); // Initialise Auth System

        // Menu Fragment:
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_menu_placeholder, new MenuFragment());
        ft.commit();
    }
}
