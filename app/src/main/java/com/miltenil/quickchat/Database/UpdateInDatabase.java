package com.miltenil.quickchat.Database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class UpdateInDatabase {

    private static final String TAG = "UpdateInDatabase";

    public UpdateInDatabase(@NonNull FirebaseFirestore db, String collectionPath, String documentName, Map<String, Object> data) {
        db.collection(collectionPath).document(documentName)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data Updated in Database");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error Updating Document", e);
                    }
                });
    }
}
