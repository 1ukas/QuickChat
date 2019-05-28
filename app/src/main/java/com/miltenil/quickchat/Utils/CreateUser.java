package com.miltenil.quickchat.Utils;

import com.google.firebase.firestore.FirebaseFirestore;
import com.miltenil.quickchat.Database.CreateInDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateUser {

    private static final String TAG = "CreateUser";
    private static final String UNREAD_MESSAGE_KEY = "unreadmessage";
    private static final String DISPLAY_NAME_KEY = "displayname";
    private static final String DISPLAY_NAME_LOWER_KEY = "displaynamelower";
    private static final String EMAIL_KEY = "email";
    private static final String EMAIL_LOWER_KEY = "emaillower";
    private static final String UNREAD_MESSAGES_KEY = "unreadmessages";
    private static final String USERS_KEY = "users";
    private static final String AVATAR_KEY = "avatar";
    private static final String AVATARS_KEY = "avatars";

    public void AddNewUser(String uid, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Put User Data:
        Map<String, Object> data = new HashMap<>();
        data.put(DISPLAY_NAME_KEY, "");
        data.put(DISPLAY_NAME_LOWER_KEY, "");
        data.put(EMAIL_KEY, email);
        data.put(EMAIL_LOWER_KEY, email.toLowerCase());
        data.put(UNREAD_MESSAGE_KEY, false);
        data.put(UNREAD_MESSAGES_KEY, 0);
        data.put(AVATAR_KEY, "");
        new CreateInDatabase(db, USERS_KEY, uid, data, false);

        // Put User Avatar Data:
        Map<String, Object> data_avatar = new HashMap<>();
        data_avatar.put(DISPLAY_NAME_KEY, "");
        data_avatar.put(AVATAR_KEY, "");
        new CreateInDatabase(db, AVATARS_KEY, uid, data_avatar, false);
    }
}
