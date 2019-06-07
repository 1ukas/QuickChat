package com.miltenil.quickchat.Utils;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class CreateMessage {

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    public CreateMessage(@NonNull FirebaseFirestore db, @NonNull FirebaseStorage storage) {
        this.db = db;
        this.storage = storage;
    }

    public void NewMessage(Uri videoUri) {

    }

}
