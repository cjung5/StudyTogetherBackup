package com.studytogether.studytogether.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.studytogether.studytogether.Models.GroupChat;
import com.studytogether.studytogether.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    // Declare groupChats
    private List<GroupChat> groupChats;

    // ChatAdapter Constructor
    public ChatAdapter(Context mContext, List<GroupChat> groupChats) {
        this.mContext = mContext;
        this.groupChats = groupChats;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Declare viewHolder and initialize to null
        RecyclerView.ViewHolder viewHolder = null;
        // Declare view and initialize to null
        View view = null;

        // Inflate with row_group_item_new view
        view = LayoutInflater.from(mContext).inflate(R.layout.row_comment,parent,false);
        // Pass the view into viewHolder
        viewHolder = new ChatViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final GroupChat groupChat = groupChats.get(position);

        ChatViewHolder chatViewHolder = (ChatViewHolder) holder;

        if(groupChat != null) {
            if(groupChat.getuserName() != null && groupChat.getContent() != null && groupChat.getuserImage() != null) {
                chatViewHolder.tvChatUserName.setText(groupChat.getuserName());
                chatViewHolder.tvChatUserComment.setText(groupChat.getContent());
                Glide.with(mContext).load(groupChat.getuserImage()).into(chatViewHolder.imgChatUser);
            }
        }
    }

    @Override
    public int getItemCount() {
        return groupChats.size();
    }

    // Create myViewHolder as RecyclerView.ViewHolder
    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvChatUserName;
        TextView tvChatUserComment;
        ImageView imgChatUser;



        // Create myViewHolder
        public ChatViewHolder(View itemView) {
            super(itemView);

            // Set the attributes with each item
            tvChatUserName = itemView.findViewById(R.id.row_user_name);
            tvChatUserComment = itemView.findViewById(R.id.row_user_comment);
            imgChatUser = itemView.findViewById(R.id.row_user_img);

        }
    }
}
