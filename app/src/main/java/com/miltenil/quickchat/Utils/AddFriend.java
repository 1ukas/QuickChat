package com.miltenil.quickchat.Utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.miltenil.quickchat.Database.DatabaseController;

import java.util.HashMap;
import java.util.Map;

public class AddFriend {

    private static final String TAG = "AddFriendActivity";
    private static final String USERS_KEY = "users";
    private static final String DISPLAY_NAME_KEY = "displayname";
    private static final String DISPLAY_NAME_LOWER_KEY = "displaynamelower";
    private static final String AVATAR_KEY = "avatar";
    private static final String EMAIL_LOWER_KEY = "emaillower";
    private static final String FRIENDS_KEY = "friends";
    private static final String AT_KEY = "@";

    private FirebaseFirestore db;

    public AddFriend(FirebaseFirestore db) {
        this.db = db;
    }

    private void CreateFriend(String friendUid, String friendDisplayName, String friendDisplayNameLower, String friendAvatarUrl, String uid) {
        DatabaseController databaseController = new DatabaseController(db);
        String collectionPath = (USERS_KEY + "/" + uid + "/" + FRIENDS_KEY);
        Map<String, Object> data = new HashMap<>();
        data.put(DISPLAY_NAME_KEY, friendDisplayName);
        data.put(DISPLAY_NAME_LOWER_KEY, friendDisplayNameLower);
        data.put(AVATAR_KEY, friendAvatarUrl);
        //new CreateInDatabase(db, collectionPath, friendUid, data, false);
        databaseController.CreateInDatabase(collectionPath, friendUid, data, false);
    }

    public void FindFriend(String friendDetails, final String uid) {
        CollectionReference users = db.collection(USERS_KEY);

        if (friendDetails.toLowerCase().contains(AT_KEY)) {
            users.whereEqualTo(EMAIL_LOWER_KEY, friendDetails.toLowerCase()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    Log.d(TAG, "Unable To Find User by Email.");
                                }
                                else {
                                    Log.d(TAG, "onComplete: FindFriend: Found by Email.");

                                    final String friendUid = task.getResult().getDocuments().get(0).getId();
                                    final String friendDisplayName = task.getResult().getDocuments().get(0).getString(DISPLAY_NAME_KEY);
                                    final String friendDisplayNameLower = task.getResult().getDocuments().get(0).getString(DISPLAY_NAME_LOWER_KEY);
                                    final String friendAvatarUrl = task.getResult().getDocuments().get(0).getString(AVATAR_KEY);
                                    CreateFriend(friendUid, friendDisplayName, friendDisplayNameLower, friendAvatarUrl, uid);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else {
            users.whereEqualTo(DISPLAY_NAME_LOWER_KEY, friendDetails.toLowerCase()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    Log.d(TAG, "Unable To Find User By Display Name.");
                                }
                                else {
                                    Log.d(TAG, "onComplete: FindFriend: Found by Display Name.");

                                    final String friendUid = task.getResult().getDocuments().get(0).getId();
                                    final String friendDisplayName = task.getResult().getDocuments().get(0).getString(DISPLAY_NAME_KEY);
                                    final String friendDisplayNameLower = task.getResult().getDocuments().get(0).getString(DISPLAY_NAME_LOWER_KEY);
                                    final String friendAvatarUrl = task.getResult().getDocuments().get(0).getString(AVATAR_KEY);
                                    CreateFriend(friendUid, friendDisplayName, friendDisplayNameLower, friendAvatarUrl, uid);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }
}
