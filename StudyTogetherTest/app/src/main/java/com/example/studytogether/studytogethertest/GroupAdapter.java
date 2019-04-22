package com.example.studytogether.studytogethertest;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.studytogether.studytogethertest.model.Group;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class GroupAdapter extends FirestoreRecyclerAdapter<Group, GroupAdapter.GroupHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public GroupAdapter(@NonNull FirestoreRecyclerOptions<Group> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull GroupHolder holder, int position, @NonNull Group model) {
        holder.groupName.setText(model.getName());
        holder.numberOfGroupMember.setText(String.valueOf(model.getNumberOfMembers()));
        holder.groupGoal.setText(model.getGoal());
        holder.groupActivate.setText(String.valueOf(model.getActive()));
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_item, viewGroup, false);

        return new GroupHolder(view);
    }

    class GroupHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        TextView numberOfGroupMember;
        TextView groupGoal;
        TextView groupActivate;

        public GroupHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.text_view_group_name);
            numberOfGroupMember = itemView.findViewById(R.id.text_view_number_of_group_member);
            groupGoal = itemView.findViewById(R.id.text_view_group_goal);
            groupActivate = itemView.findViewById(R.id.text_view_group_active);
        }
    }
}
