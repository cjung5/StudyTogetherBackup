package com.studytogether.studytogether.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.studytogether.studytogether.Activities.GroupChatActivity;
import com.studytogether.studytogether.Activities.GroupDetailActivity;
import com.studytogether.studytogether.Models.Group;
import com.studytogether.studytogether.R;

import java.util.List;

// GroupAdapter for recyclerView
public class GroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // View Type
    private static final int TYPE_STUDY = 1;
    private static final int TYPE_TUTOR = 2;

    // Context
    private Context mContext;
    // Declare filteredGroup
    private List<Group> filteredGroup;

    // groupAdapter Constructor
    public GroupAdapter(Context mContext, List<Group> srcGroups) {
        this.mContext = mContext;
        this.filteredGroup = srcGroups  ;
    }

    // Get view type
    @Override
    public int getItemViewType(int position) {
        // Take a specific group
        final Group group = filteredGroup.get(position);
        // Check the group whether it is for tutoring or not
        if (group.getTutor().toLowerCase().contains("true")) {
            return TYPE_TUTOR;
        } else {
            return TYPE_STUDY;
        }
    }

    // Recycler viewHolder
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Declare viewHolder and initialize to null
        RecyclerView.ViewHolder viewHolder = null;
        // Declare view and initialize to null
        View view = null;

        // Seperate view by viewType
        switch (viewType) {
            case TYPE_STUDY:
                // Inflate with row_group_item_new view
                view = LayoutInflater.from(mContext).inflate(R.layout.row_group_item_new,parent,false);
                // Pass the view into viewHolder
                viewHolder = new MyViewHolder(view);
                break;

            case TYPE_TUTOR:
                // Inflate with row_group_item_tutor view
                view = LayoutInflater.from(mContext).inflate(R.layout.row_group_item_tutor,parent,false);
                // Pass the view into viewHolder
                viewHolder = new MyViewHolderTutor(view);
                break;
        }
        return viewHolder;
    }

    // Bind viewHolder
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        // Take the specific group
        final Group group = filteredGroup.get(position);

        // Seperate holder by viewType
        // Pass the information of the group into viewHolder
        switch (holder.getItemViewType()) {
            case TYPE_STUDY:
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                myViewHolder.tvGroupName.setText(group.getGroupName());
                myViewHolder.tvGroupPlace.setText(group.getGroupPlace());
                myViewHolder.tvNumOfGroupMembers.setText(group.getNum_of_group_members());
                myViewHolder.tvStartTimeInput.setText(group.getStartTime());
                myViewHolder.tvEndTimeInput.setText(group.getEndTime());
                Glide.with(mContext).load(group.getGroupPicture()).into(myViewHolder.imgGroup);
                Glide.with(mContext).load(group.getGroupOwnerPhoto()).into(myViewHolder.imgOwnerProfile);
                break;
            case TYPE_TUTOR:
                MyViewHolderTutor myViewHolderTutor = (MyViewHolderTutor) holder;
                myViewHolderTutor.tvGroupName.setText(group.getGroupName());
                myViewHolderTutor.tvGroupPlace.setText(group.getGroupPlace());
                myViewHolderTutor.tvNumOfGroupMembers.setText(group.getNum_of_group_members());
                myViewHolderTutor.tvStartTimeInput.setText(group.getStartTime());
                myViewHolderTutor.tvEndTimeInput.setText(group.getEndTime());
                Glide.with(mContext).load(group.getGroupPicture()).into(myViewHolderTutor.imgGroup);
                Glide.with(mContext).load(group.getGroupOwnerPhoto()).into(myViewHolderTutor.imgOwnerProfile);
                break;
        }
    }

    // Counts Items
    @Override
    public int getItemCount() {
        return filteredGroup.size();
    }


    // Create myViewHolder as RecyclerView.ViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // Declare all attributes as same as TYPE_STUDY view
        TextView tvGroupName;
        TextView tvGroupPlace;
        TextView tvNumOfGroupMembers;
        TextView tvStartTimeInput;
        TextView tvEndTimeInput;
        ImageView imgGroup;
        ImageView imgOwnerProfile;
        CardView cardView;


        // Create myViewHolder
        public MyViewHolder(View itemView) {
            super(itemView);

            // Set the attributes with each item
            tvGroupName = itemView.findViewById(R.id.row_group_name);
            tvGroupPlace = itemView.findViewById(R.id.row_group_place);
            tvNumOfGroupMembers = itemView.findViewById(R.id.row_num_of_group_members);
            tvStartTimeInput = itemView.findViewById(R.id.row_start_time_input);
            tvEndTimeInput = itemView.findViewById(R.id.row_end_time_input);
            imgGroup = itemView.findViewById(R.id.row_group_img);
            imgOwnerProfile = itemView.findViewById(R.id.row_owner_profile_img);
            cardView = itemView.findViewById(R.id.cardview_group);

            // Click Listener to touch each group
            // Progress into the groupDetail activity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent groupChatActivity = new Intent(mContext, GroupChatActivity.class);
                    int position = getAdapterPosition();
                    groupChatActivity.putExtra("GroupPosition",position);
                    groupChatActivity.putExtra("GroupKey",filteredGroup.get(position).getGroupKey());

                    groupChatActivity.putExtra("GroupName",filteredGroup.get(position).getGroupName());
                    groupChatActivity.putExtra("GroupPlace",filteredGroup.get(position).getGroupPlace());
                    groupChatActivity.putExtra("GroupGoal",filteredGroup.get(position).getGroupGoal());
                    groupChatActivity.putExtra("GroupImg",filteredGroup.get(position).getGroupPicture());
                    groupChatActivity.putExtra("groupKey",filteredGroup.get(position).getGroupKey());
                    long timestamp = (long) filteredGroup.get(position).getTimeStamp();
                    groupChatActivity.putExtra("addedDate", timestamp);

                    // start the GroupDetailActivity
                    mContext.startActivity(groupChatActivity);
                }
            });
        }
    }

    // Create myViewHolderTutor as RecyclerView.ViewHolder
    public class MyViewHolderTutor extends RecyclerView.ViewHolder {
        // Declare all attributes as same as TYPE_TUTOR view
        TextView tvGroupName;
        TextView tvGroupPlace;
        TextView tvNumOfGroupMembers;
        TextView tvStartTimeInput;
        TextView tvEndTimeInput;
        ImageView imgGroup;
        ImageView imgOwnerProfile;
        CardView cardView;


        // Create myViewHolderTutor
        public MyViewHolderTutor(View itemView) {
            super(itemView);

            // Set the attributes with each item
            tvGroupName = itemView.findViewById(R.id.row_group_name);
            tvGroupPlace = itemView.findViewById(R.id.row_group_place);
            tvNumOfGroupMembers = itemView.findViewById(R.id.row_num_of_group_members);
            tvStartTimeInput = itemView.findViewById(R.id.row_start_time_input);
            tvEndTimeInput = itemView.findViewById(R.id.row_end_time_input);
            imgGroup = itemView.findViewById(R.id.row_group_img);
            imgOwnerProfile = itemView.findViewById(R.id.row_owner_profile_img);
            cardView = itemView.findViewById(R.id.cardview_group);

            // Click Listener to touch each group
            // Progress into the groupDetail activity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent groupChatActivity = new Intent(mContext, GroupChatActivity.class);
                    int position = getAdapterPosition();
                    groupChatActivity.putExtra("GroupPosition",position);
                    groupChatActivity.putExtra("GroupKey",filteredGroup.get(position).getGroupKey());

                    groupChatActivity.putExtra("GroupName",filteredGroup.get(position).getGroupName());
                    groupChatActivity.putExtra("GroupPlace",filteredGroup.get(position).getGroupPlace());
                    groupChatActivity.putExtra("GroupGoal",filteredGroup.get(position).getGroupGoal());
                    groupChatActivity.putExtra("GroupImg",filteredGroup.get(position).getGroupPicture());
                    groupChatActivity.putExtra("groupKey",filteredGroup.get(position).getGroupKey());
                    long timestamp = (long) filteredGroup.get(position).getTimeStamp();
                    groupChatActivity.putExtra("addedDate", timestamp);

                    // start the GroupDetailActivity
                    mContext.startActivity(groupChatActivity);
                }
            });
        }
    }
}