package com.miltenil.quickchat.Utils;

import com.google.firebase.firestore.FirebaseFirestore;
import com.miltenil.quickchat.Database.DatabaseController;

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

    public boolean AddNewUser(String uid, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DatabaseController databaseController = new DatabaseController(db);

        // Create User Data:
        Map<String, Object> data = createUserData(email);
        databaseController.CreateInDatabase(USERS_KEY, uid, data, false);

        // Create Avatar Data:
        Map<String, Object> data_avatar = createAvatarData();
        databaseController.CreateInDatabase(AVATARS_KEY, uid, data_avatar, false);
        return true;
    }

    public Map<String, Object> createUserData(String email) {
        Map<String, Object> data = new HashMap<>();
        data.put(DISPLAY_NAME_KEY, "");
        data.put(DISPLAY_NAME_LOWER_KEY, "");
        data.put(EMAIL_KEY, email);
        data.put(EMAIL_LOWER_KEY, email.toLowerCase());
        data.put(UNREAD_MESSAGE_KEY, false);
        data.put(UNREAD_MESSAGES_KEY, 0);
        data.put(AVATAR_KEY, "");
        return data;
    }

    public Map<String, Object> createAvatarData() {
        Map<String, Object> data_avatar = new HashMap<>();
        data_avatar.put(DISPLAY_NAME_KEY, "");
        data_avatar.put(AVATAR_KEY, "");
        return data_avatar;
    }
}
