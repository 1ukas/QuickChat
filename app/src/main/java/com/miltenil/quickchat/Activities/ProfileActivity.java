package com.miltenil.quickchat.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.miltenil.quickchat.Database.CreateInDatabase;
import com.miltenil.quickchat.Database.UpdateInDatabase;
import com.miltenil.quickchat.Fragments.MenuFragment;
import com.miltenil.quickchat.Interfaces.DataListener;
import com.miltenil.quickchat.R;
import com.miltenil.quickchat.Storage.UploadToStorage;
import com.miltenil.quickchat.Utils.LoadImageFromURLAsync;
import com.miltenil.quickchat.Utils.RescaleImage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final String TITLE_KEY = "Profile";
    private static final String USERS_KEY = "users";
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int GET_FROM_GALLERY = 3;
    private static final String DISPLAYNAME_KEY = "displayname";
    private static final String EMAIL_KEY = "email";
    private static final String AVATAR_KEY = "avatar";
    private static final String AVATARS_COLLECTION_KEY = "avatars";
    private static final int MAX_WIDTH = 200;
    private static final int MAX_HEIGHT = 200;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    Button changePassButton;
    EditText passwordField;
    EditText emailField;
    TextView successText;
    ImageView profileAvatar;

    private String passString;

    DataListener dataListener = new DataListener() {
        @Override
        public void onGetUriResult(Uri uri) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> data = new HashMap<>();
            data.put(AVATAR_KEY, uri.toString());
            // Update Avatar URI in User Profile Document
            new UpdateInDatabase(db, USERS_KEY, mAuth.getUid(), data);
            // Update Avatar URI in Avatar Document
            new UpdateInDatabase(db, AVATARS_COLLECTION_KEY, mAuth.getUid(), data);
            // Update Avatar URI in User Firebase Auth
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(uri)
                    .build();

            currentUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User Avatar Updated.");
                            }
                        }
                    });
        }
    };

    private void SetProfileData(String displayName, String email) {
        final TextView displayNameView = findViewById(R.id.profile_displayname);
        final TextView emailView = findViewById(R.id.profile_email);
        displayNameView.setText(displayName);
        emailView.setText(email);
        if (currentUser.getPhotoUrl() != null) {
            new LoadImageFromURLAsync().execute(profileAvatar, currentUser.getPhotoUrl().toString());
        }
    }

    private void GetProfileData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference users = db.collection(USERS_KEY);
        users.document(mAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: GetProfileData: User Found.");
                            String displayName = task.getResult().getString(DISPLAYNAME_KEY);
                            String email = task.getResult().getString(EMAIL_KEY);
                            SetProfileData(displayName, email);
                        }
                        else {
                            Log.w(TAG, "onComplete: GetProfileData: Error Getting User Document: ", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] byteData = baos.toByteArray();
                byte[] rescaledData = new RescaleImage().decodeResource(byteData, MAX_WIDTH, MAX_HEIGHT);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                new UploadToStorage(storage).UploadByteArray(mAuth.getUid(), AVATARS_COLLECTION_KEY, rescaledData, dataListener);

                Bitmap rescaledBitmap = BitmapFactory.decodeByteArray(rescaledData, 0, rescaledData.length);
                profileAvatar.setImageBitmap(rescaledBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) { // If the user is not logged in, open LogInActivity
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
        }
        else if (currentUser.getDisplayName().equals("")) {
            Intent intent = new Intent(this, DisplayNameActivity.class);
            startActivity(intent);
        }
    }

    View.OnClickListener passwordButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            passwordField.setVisibility(View.VISIBLE);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String passwordString = passwordField.getText().toString();
                    if (passwordString.length() < MIN_PASSWORD_LENGTH) {
                        Toast.makeText(ProfileActivity.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        ChangePassword(passwordString);
                    }
                }
            });
        }
    };

    View.OnClickListener reAuthButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String emailInput = emailField.getText().toString();
            final String passwordInput = passwordField.getText().toString();

            AuthCredential credential = EmailAuthProvider
                    .getCredential(emailInput, passwordInput);

            // Prompt the user to re-provide their sign-in credentials
            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User re-authenticated.");

                                currentUser.updatePassword(passString)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User password updated.");

                                                    passwordField.setVisibility(View.GONE);
                                                    emailField.setVisibility(View.GONE);
                                                    changePassButton.setVisibility(View.GONE);
                                                    successText.setText(R.string.pass_changed);
                                                }
                                            }
                                        });
                            }
                            else {
                                Toast.makeText(ProfileActivity.this, "Re-authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ProfileActivity.this.setTitle(TITLE_KEY);
        mAuth = FirebaseAuth.getInstance(); // Initialise Auth System

        // Menu Fragment:
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_menu_placeholder, new MenuFragment());
        ft.commit();

        GetProfileData();

        passwordField = findViewById(R.id.profile_password_field);
        changePassButton = findViewById(R.id.profile_change_password_btn);
        changePassButton.setOnClickListener(passwordButtonListener);
        profileAvatar = findViewById(R.id.profile_avatar_img);
        profileAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
    }

    private void ChangePassword (String passwordString) {
        passString = passwordString;
        emailField = findViewById(R.id.profile_email_field);
        successText = findViewById(R.id.profile_success);
        successText.setText(R.string.reauth_message);
        successText.setVisibility(View.VISIBLE);
        emailField.setVisibility(View.VISIBLE);
        passwordField.getText().clear();
        changePassButton.setText(R.string.reauth_profile_prompt);
        changePassButton.setOnClickListener(reAuthButtonListener);
    }
}
