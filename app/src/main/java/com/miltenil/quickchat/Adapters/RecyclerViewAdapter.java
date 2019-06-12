package com.miltenil.quickchat.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.miltenil.quickchat.Activities.FriendsListActivity;
import com.miltenil.quickchat.Activities.MainActivity;
import com.miltenil.quickchat.Activities.VideoPlayActivity;
import com.miltenil.quickchat.Database.DatabaseController;
import com.miltenil.quickchat.Utils.LoadImageFromURLAsync;
import com.miltenil.quickchat.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private static final String USERS_COLLECTION_KEY = "users";
    private static final String SENT_MESSAGES_COLLECTION_KEY = "sent-messages";
    private static final String RECEIVED_MESSAGES_COLLECTION_KEY = "received-messages";
    private static final String RECIPIENT_UID_KEY = "recipientUID";
    private static final String VIDEO_URI_KEY = "videoURI";
    private static final String SENDER_UID_KEY = "senderUID";
    private static final String READ_KEY = "read";
    private static final String DISPLAY_NAME_LOWER_KEY = "displaynamelower";
    private static final String DATE_KEY = "date";
    public static final String FRIENDS_KEY = "friends";

    private ArrayList<String> mFriendImages = new ArrayList<>();
    private ArrayList<String> mFriendNames = new ArrayList<>();
    private ArrayList<Boolean> mReadMessages = new ArrayList<>();
    private ArrayList<String> mVideoUris = new ArrayList<>();
    private ArrayList<String> mDocumentIDs = new ArrayList<>();
    private ArrayList<String> mMessageDates = new ArrayList<>();
    private boolean mCreateMessage = false;
    private boolean mInbox = false;
    private String uid;
    private Uri videoUri;
    private Context mContext;

    public enum ViewHolderType {TYPE_FRIENDS_LIST, TYPE_INBOX}

    //FriendsListActivity:
    public RecyclerViewAdapter(ArrayList<String> mFriendImages, ArrayList<String> mFriendNames,
                               ArrayList<String> mDocumentIDs, Context mContext) {
        this.mFriendImages = mFriendImages;
        this.mFriendNames = mFriendNames;
        this.mDocumentIDs = mDocumentIDs;
        this.mContext = mContext;
    }

    //When Creating a Message:
    public RecyclerViewAdapter(ArrayList<String> mFriendImages, ArrayList<String> mFriendNames,
                               String uid, Uri videoUri, Context mContext) {
        this.mFriendImages = mFriendImages;
        this.mFriendNames = mFriendNames;
        this.mCreateMessage = true;
        this.uid = uid;
        this.videoUri = videoUri;
        this.mContext = mContext;
    }

    //InboxActivity (MainActivity):
    public RecyclerViewAdapter(ArrayList<String> mFriendImages, ArrayList<String> mFriendNames,
                               ArrayList<Boolean> mReadMessages, ArrayList<String> mVideoUris,
                               ArrayList<String> mDocumentIDs, ArrayList<String> mMessageDates,
                               Context mContext) {
        this.mFriendImages = mFriendImages;
        this.mFriendNames = mFriendNames;
        this.mReadMessages = mReadMessages;
        this.mVideoUris = mVideoUris;
        this.mDocumentIDs = mDocumentIDs;
        this.mMessageDates = mMessageDates;
        this.mInbox = true;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mInbox) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_inboxlist, viewGroup, false);
            return new ViewHolder(view, ViewHolderType.TYPE_INBOX);
        }
        else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
            return new ViewHolder(view, ViewHolderType.TYPE_FRIENDS_LIST);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called.");
        if (mInbox) {
            if (!mFriendImages.get(i).isEmpty()) { // If there is an avatar saved, use it.
                new LoadImageFromURLAsync().execute(viewHolder.friendImage, mFriendImages.get(i));
            }
            if (!mReadMessages.get(i)) { // If message is unread set the text to bold.
                viewHolder.friendName.setTypeface(viewHolder.friendName.getTypeface(), Typeface.BOLD);
            }
            String senderText = ("FROM: " + mFriendNames.get(i));
            viewHolder.friendName.setText(senderText);
            viewHolder.sendDate.setText(mMessageDates.get(i));
            viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    HandleInboxOnClick(mVideoUris.get(i), mDocumentIDs.get(i), mReadMessages.get(i));
                    v.setEnabled(true);
                }
            });
        }
        else {
            if (!mFriendImages.get(i).isEmpty()) { // If there is an avatar saved, use it.
                new LoadImageFromURLAsync().execute(viewHolder.friendImage, mFriendImages.get(i));
            }
            viewHolder.friendName.setText(mFriendNames.get(i));
            viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    Log.d(TAG, "onClick: clicked on: " + mFriendNames.get(i));
                    if (mCreateMessage) {
                        HandleCreateMessageOnClick(mFriendNames.get(i));
                    }
                    else {
                        viewHolder.removeFriend.setVisibility(View.VISIBLE);
                        v.setEnabled(true);
                        viewHolder.removeFriend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.setEnabled(false);
                                RemoveFriend(mDocumentIDs.get(i));
                                v.setVisibility(View.GONE);
                            }
                        });
                        //Toast.makeText(mContext, mFriendNames.get(i), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void RemoveFriend(String friendUID) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String collectionPath = (USERS_COLLECTION_KEY + "/" + mAuth.getUid() + "/" + FRIENDS_KEY);
        db.collection(collectionPath).document(friendUID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        ((Activity)mContext).recreate();
                        Toast.makeText(mContext, "Friend Removed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                        Toast.makeText(mContext, "Something Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void HandleInboxOnClick(String videoUri, String documentID, Boolean read) {
        Intent intent = new Intent(mContext, VideoPlayActivity.class);
        intent.putExtra("videoUri", videoUri);
        intent.putExtra("documentID", documentID);
        intent.putExtra("read", read);
        mContext.startActivity(intent);
    }

    private void SendMessage(String friendUid) {
        DatabaseController databaseController = new DatabaseController(FirebaseFirestore.getInstance());
        String sentMessageCollPath = (USERS_COLLECTION_KEY + "/" + uid + "/" + SENT_MESSAGES_COLLECTION_KEY);
        Date date = Calendar.getInstance().getTime();
        DateFormat format = SimpleDateFormat.getDateInstance();
        String dateString = format.format(date);
        Map<String, Object> sentMessageData = new HashMap<>();
        sentMessageData.put(RECIPIENT_UID_KEY, friendUid);
        sentMessageData.put(VIDEO_URI_KEY, videoUri.toString());
        sentMessageData.put(DATE_KEY, dateString);

        databaseController.CreateInDatabase(sentMessageCollPath, UUID.randomUUID().toString(), sentMessageData, false);

        String receivedMessageCollPath = (USERS_COLLECTION_KEY + "/" + friendUid + "/" + RECEIVED_MESSAGES_COLLECTION_KEY);
        Map<String, Object> receivedMessageData = new HashMap<>();
        receivedMessageData.put(SENDER_UID_KEY, uid);
        receivedMessageData.put(VIDEO_URI_KEY, videoUri.toString());
        receivedMessageData.put(READ_KEY, false);
        receivedMessageData.put(DATE_KEY, dateString);

        databaseController.CreateInDatabase(receivedMessageCollPath, UUID.randomUUID().toString(), receivedMessageData, false);
        Toast.makeText(mContext, "Message Sent", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mContext, MainActivity.class);
        mContext.startActivity(intent);
    }

    private void HandleCreateMessageOnClick(String mFriendName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference users = db.collection(USERS_COLLECTION_KEY);
        users.whereEqualTo(DISPLAY_NAME_LOWER_KEY, mFriendName.toLowerCase()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String friendUid = document.getId();
                                SendMessage(friendUid);
                            }
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mFriendNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView friendImage;
        TextView friendName;
        TextView sendDate;
        Button removeFriend;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView, ViewHolderType viewType) {
            super(itemView);
            switch (viewType) {
                case TYPE_FRIENDS_LIST:
                    friendImage = itemView.findViewById(R.id.friend_image);
                    friendName = itemView.findViewById(R.id.friend_name);
                    parentLayout = itemView.findViewById(R.id.parent_layout);
                    removeFriend = itemView.findViewById(R.id.remove_friend_btn);
                    break;
                case TYPE_INBOX:
                    friendImage = itemView.findViewById(R.id.friend_image);
                    friendName = itemView.findViewById(R.id.sender_name);
                    parentLayout = itemView.findViewById(R.id.parent_layout);
                    sendDate = itemView.findViewById(R.id.send_date);
                    break;
            }
        }
    }
}



