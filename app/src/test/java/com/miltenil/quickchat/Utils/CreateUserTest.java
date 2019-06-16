package com.miltenil.quickchat.Utils;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CreateUserTest {

    private CreateUser createUser;
    private static final String UNREAD_MESSAGE_KEY = "unreadmessage";
    private static final String DISPLAY_NAME_KEY = "displayname";
    private static final String DISPLAY_NAME_LOWER_KEY = "displaynamelower";
    private static final String EMAIL_KEY = "email";
    private static final String EMAIL_LOWER_KEY = "emaillower";
    private static final String UNREAD_MESSAGES_KEY = "unreadmessages";
    private static final String AVATAR_KEY = "avatar";

    @Before
    public void setup() {
        createUser = new CreateUser();
    }

    @Test
    public void createUserData() {
        Map<String, Object> compareData = new HashMap<>();
        compareData.put(DISPLAY_NAME_KEY, "");
        compareData.put(DISPLAY_NAME_LOWER_KEY, "");
        compareData.put(EMAIL_KEY, "test@test.com");
        compareData.put(EMAIL_LOWER_KEY, "test@test.com".toLowerCase());
        compareData.put(UNREAD_MESSAGE_KEY, false);
        compareData.put(UNREAD_MESSAGES_KEY, 0);
        compareData.put(AVATAR_KEY, "");

        assertEquals(compareData, createUser.createUserData("test@test.com"));
    }

    @Test
    public void createAvatarData() {
        Map<String, Object> compareData = new HashMap<>();
        compareData.put(DISPLAY_NAME_KEY, "");
        compareData.put(AVATAR_KEY, "");

        assertEquals(compareData, createUser.createAvatarData());
    }
}