package com.miltenil.quickchat.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miltenil.quickchat.Utils.LoadImageFromURLAsync;
import com.miltenil.quickchat.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> mFriendImages = new ArrayList<>();
    private ArrayList<String> mFriendNames = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> mFriendImages, ArrayList<String> mFriendNames, Context mContext) {
        this.mFriendImages = mFriendImages;
        this.mFriendNames = mFriendNames;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called.");
        new LoadImageFromURLAsync().execute(viewHolder.friendImage, mFriendImages.get(i));
        viewHolder.friendName.setText(mFriendNames.get(i));
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + mFriendNames.get(i));
                Toast.makeText(mContext, mFriendNames.get(i), Toast.LENGTH_SHORT).show();
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
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            friendImage = itemView.findViewById(R.id.friend_image);
            friendName = itemView.findViewById(R.id.friend_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}



