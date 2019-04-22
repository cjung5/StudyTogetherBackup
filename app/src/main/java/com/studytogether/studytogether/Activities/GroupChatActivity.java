package com.studytogether.studytogether.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.studytogether.studytogether.Adapters.ChatAdapter;
import com.studytogether.studytogether.Models.GroupChat;
import com.studytogether.studytogether.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GroupChatActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    // Create items
    TextView chatGroupName;
    ImageView chatGroupImage;
    EditText userComment;
    Toolbar toolbar;
    Button btnAddComment;

    List<GroupChat> commentList;

    private RecyclerView commentRecyclerView;
    private RecyclerView.Adapter chatAdapter;
    private RecyclerView.LayoutManager chatLayoutManager;


    private CollapsingToolbarLayout collapsingToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the contentView with group_detail
        setContentView(R.layout.activity_group_chat);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        userComment = findViewById(R.id.chat_editText);
        btnAddComment = findViewById(R.id.chat_add_button);
        commentRecyclerView  = findViewById(R.id.commentRV);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        chatLayoutManager = new LinearLayoutManager(this);
        commentRecyclerView.setLayoutManager(chatLayoutManager);


        Intent intent = getIntent();
        String chatGroupName = intent.getExtras().getString("GroupName");
        String chatGroupPlace = intent.getExtras().getString("GroupPlace");
        String chatGroupGoal = intent.getExtras().getString("GroupGoal");
        String groupCreated = timestampToString(getIntent().getExtras().getLong("addedDate"));
        String groupKey = intent.getExtras().getString("GroupKey");



        final Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.chat_collapsing_toolbar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.toolbar_text);
        collapsingToolbar.setTitle(chatGroupName);

        final ImageView imageView = findViewById(R.id.chat_backdrop);
        String imageUrl = getIntent().getStringExtra("GroupImg");
        Glide.with(this).load(imageUrl).apply(RequestOptions.centerCropTransform()).into(imageView);

        final AppBarLayout appbarLayout = (AppBarLayout)findViewById(R.id.chat_appbar_layout);
        FloatingActionButton groupDetail = (FloatingActionButton) findViewById(R.id.group_detail_btn);
        // If the floating action button is clicked,
        groupDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent groupDetailActivity = new Intent(getApplicationContext(), GroupDetailActivity.class);

                // passing data to the GroupDetailActivity
                groupDetailActivity.putExtra("GroupName", chatGroupName);
                groupDetailActivity.putExtra("GroupPlace", chatGroupPlace);
                groupDetailActivity.putExtra("GroupGoal", chatGroupGoal);
                groupDetailActivity.putExtra("GroupImg", imageUrl);
                groupDetailActivity.putExtra("GroupKey", groupKey);
                groupDetailActivity.putExtra("addedDate", groupCreated);
                startActivity(groupDetailActivity);
            }
        });


        updateComment(groupKey);


        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnAddComment.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = firebaseDatabase.getReference("GroupChat").child(groupKey).push();
                String comment_content = userComment.getText().toString();
                String userId = firebaseUser.getUid();
                String userName = firebaseUser.getDisplayName();
                String userImage = firebaseUser.getPhotoUrl().toString();
                GroupChat groupChat = new GroupChat(comment_content,userId,userImage,userName);

                commentReference.setValue(groupChat).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        userComment.setText("");
                        btnAddComment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("fail to add comment : "+e.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    private void updateComment(String groupKey) {
        // Take a reference of GroupChat
        DatabaseReference commentReference = firebaseDatabase.getReference("GroupChat").child(groupKey);


        // specify an adapter
        commentReference.addValueEventListener(new ValueEventListener() {
            // Detect the changes
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Reinitialize the groupList
                commentList = new ArrayList<>();
                // Loop whole comments
                for (DataSnapshot commentsnap: dataSnapshot.getChildren()) {

                    GroupChat comment = commentsnap.getValue(GroupChat.class);
                    // Add comment
                    commentList.add(comment) ;
                }

                // Set recyclerView using chatAdapter
                chatAdapter = new ChatAdapter(GroupChatActivity.this,commentList);
                commentRecyclerView.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    // Get server time and convert into String
    private String timestampToString(long time) {

        // Get time
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        // Reform the time and cast into String type
        String date = DateFormat.format("MM-dd-yyyy",calendar).toString();
        return date;


    }

    // Show message to the user
    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }
}

