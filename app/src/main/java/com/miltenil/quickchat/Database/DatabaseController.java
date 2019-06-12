package com.miltenil.quickchat.Database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Map;

public class DatabaseController {

    private static final String TAG = "DatabaseController";

    private FirebaseFirestore db;

    public DatabaseController(@NonNull FirebaseFirestore db) {
        this.db = db;
    }

    public void CreateInDatabase(String collectionPath, String documentName, Map<String, Object> data, boolean merge) {
        if (merge) {
            db.collection(collectionPath).document(documentName)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Document Created in Database");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error Writing Document", e);
                        }
                    });
        }
        else {
            db.collection(collectionPath).document(documentName)
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Document Created in Database");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error Writing Document", e);
                        }
                    });
        }
    }

    public void UpdateInDatabase(String collectionPath, String documentName, Map<String, Object> data) {
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
