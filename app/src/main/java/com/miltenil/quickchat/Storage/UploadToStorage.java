package com.miltenil.quickchat.Storage;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.miltenil.quickchat.Interfaces.IDataListener;

public class UploadToStorage {

    private static final String TAG = "UploadByteArray";
    private FirebaseStorage storage;

    public UploadToStorage(@NonNull FirebaseStorage storage) {
        this.storage = storage;
    }

    public void UploadByteArray(String uid, String directory, byte[] data, final IDataListener IDataListener) {
        StorageReference storageRef = storage.getReference();
        String filePath = (directory + "/" + uid + ".png");
        final StorageReference directoryRef = storageRef.child(filePath);

        UploadTask uploadTask = directoryRef.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return directoryRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    IDataListener.onGetUriResult(downloadUri);
                } else {
                    Log.w(TAG, "onComplete: Failed to get Uri", task.getException());
                }
            }
        });
    }

    public void UploadFile(String uid, String directory, Uri fileUri, final IDataListener IDataListener) {
        StorageReference storageRef = storage.getReference();
        String filePath = (directory + "/" + uid + "/" + fileUri.getLastPathSegment());
        final StorageReference directoryRef = storageRef.child(filePath);

        UploadTask uploadTask = directoryRef.putFile(fileUri);

        Task<Uri> urlTask = uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                IDataListener.onProgressUpdate((int)progress);
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return directoryRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    IDataListener.onGetUriResult(downloadUri);
                } else {
                    Log.w(TAG, "onComplete: Failed to get Uri", task.getException());
                }
            }
        });
    }
}
