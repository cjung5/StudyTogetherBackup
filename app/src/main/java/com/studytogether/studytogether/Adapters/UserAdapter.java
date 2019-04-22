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
import com.studytogether.studytogether.Models.User;
import com.studytogether.studytogether.R;

import java.util.List;

public class UserAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    // Declare groupChats
    private List<User> users;

    // ChatAdapter Constructor
    public UserAdapter(Context mContext, List<User> users) {
        this.mContext = mContext;
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Declare viewHolder and initialize to null
        RecyclerView.ViewHolder viewHolder = null;
        // Declare view and initialize to null
        View view = null;

        // Inflate with row_group_item_new view
        view = LayoutInflater.from(mContext).inflate(R.layout.row_user,parent,false);
        // Pass the view into viewHolder
        viewHolder = new ChatViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final User user = users.get(position);

        ChatViewHolder chatViewHolder = (ChatViewHolder) holder;

        if(user != null) {
            if(user.getuserName() != null && user.getUserEmail() != null && user.getuserImage() != null) {
                chatViewHolder.tvUserUserName.setText(user.getuserName());
                chatViewHolder.tvUserUserEmail.setText(user.getUserEmail());
                Glide.with(mContext).load(user.getuserImage()).into(chatViewHolder.imgUserUserPhoto);
            }
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // Create myViewHolder as RecyclerView.ViewHolder
    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserUserName;
        TextView tvUserUserEmail;
        ImageView imgUserUserPhoto;



        // Create myViewHolder
        public ChatViewHolder(View itemView) {
            super(itemView);

            // Set the attributes with each item
            tvUserUserName = itemView.findViewById(R.id.row_big_user_name);
            tvUserUserEmail = itemView.findViewById(R.id.row_user_email);
            imgUserUserPhoto = itemView.findViewById(R.id.row_user_image);

        }
    }
}
