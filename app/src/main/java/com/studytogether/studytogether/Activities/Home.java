package com.studytogether.studytogether.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.studytogether.studytogether.Fragments.HomeFragment;
import com.studytogether.studytogether.Fragments.ProfileFragment;
import com.studytogether.studytogether.Fragments.SettingsFragment;
import com.studytogether.studytogether.Fragments.TutorFragment;
import com.studytogether.studytogether.Models.Group;
import com.studytogether.studytogether.R;
import com.studytogether.studytogether.Adapters.GroupAdapter;

import java.util.ArrayList;
import java.util.List;


// Home Activity
public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Declare the groupAdapter
    GroupAdapter adapter;
    // Declare the groupList and initialize to empty list
    List<Group> groupList = new ArrayList<>();

    // Flags
    private static final int PReqCode = 2;
    private static final int REQUESCODE = 2;

    // Firebase
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    // Dialog for popup
    Dialog popAddGroup;

    // Items
    ImageView popupUserImage, popupGroupImage, popupAddBtn;
    TextView popupGroupName, popupGroupGoal, popupGroupPlace, popupNumOfGroupMembers, popupStartTimeInput, popupEndTimeInput;
    ProgressBar popupClickProgress;
    Switch popupSwitch;
    Boolean tutoring = false;
    private Uri pickedImgUri = null;

    // Initialize the groupAdapter
    private void initGroupAdapter() {
        // If groupAdapter is null
        if(adapter == null ) {
            // Reinitialize the groupAdapter
            adapter = new GroupAdapter(this, groupList);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the contentView as activity_home2
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Set up the appBar
        setSupportActionBar(toolbar);

        // Authorization
        mAuth = FirebaseAuth.getInstance();
        // Identify the current user
        currentUser = mAuth.getCurrentUser();

        // Initialize the popup
        iniPopup();
        // Set up the popup Image Click
        setupPopupImageClick();

        // Set up the floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        // If the floating action button is clicked, show the popup
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddGroup.show();
            }
        });

        // Drawer - Left side slide that contains the current user info and nevigations such as Home, Profile, Sign out
        // Set up the drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Set up action bar toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        // Sync the toggle
        toggle.syncState();

        // Set up the navigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();
        // set the home fragment as the default one
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
    }


    // Image selection
    private void setupPopupImageClick() {

        popupGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // It is called when a user clicked image spot in popup
                // It is required permission
                checkAndRequestForPermission();
            }
        });
    }


    // Ask request permission
    private void checkAndRequestForPermission() {

        // If the user allows permission, access into user's gallery
        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(Home.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(Home.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else
            // If the user allowed the permission already, open the user's gallery
            openGallery();
    }


    // Open the user's gallery
    private void openGallery() {

        // Get intent
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        // set up the intent type
        galleryIntent.setType("image/*");
        // Get into gallery
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    // Grab the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If all flags is good with no data
        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {

            // Get the image data
            pickedImgUri = data.getData();
            // Show up into popup the image
            popupGroupImage.setImageURI(pickedImgUri);
        }
    }


    // Initialize popup
    private void iniPopup() {

        // Create a new dialog for popup
        popAddGroup = new Dialog(this);
        // Set up the contentView into popup
        popAddGroup.setContentView(R.layout.popup_add_post);
        // Window setting
        popAddGroup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddGroup.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddGroup.getWindow().getAttributes().gravity = Gravity.TOP;

        // Initialize the popup items
        popupUserImage = popAddGroup.findViewById(R.id.popup_user_img);
        popupGroupImage = popAddGroup.findViewById(R.id.popup_img);
        popupGroupName = popAddGroup.findViewById(R.id.popup_group_name);
        popupGroupGoal = popAddGroup.findViewById(R.id.popup_group_goal);
        popupGroupPlace = popAddGroup.findViewById(R.id.popup_group_place);
        popupNumOfGroupMembers = popAddGroup.findViewById(R.id.popup_num_of_group_members);
        popupStartTimeInput = popAddGroup.findViewById(R.id.popup_start_time_input);
        popupEndTimeInput = popAddGroup.findViewById(R.id.popup_end_time_input);
        popupAddBtn = popAddGroup.findViewById(R.id.popup_add);
        popupClickProgress = popAddGroup.findViewById(R.id.popup_progressBar);
        popupSwitch = popAddGroup.findViewById(R.id.popup_switch);
        popupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Get the switch value and store into tutoring
                tutoring = isChecked;
            }
        });
        // Grab the image
        Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(popupUserImage);

        // Listener for add-button on popup
        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When the group is added, let the add-button disappear and the progress-button show up.
                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                // If all inputs is typed,
                if (!popupGroupName.getText().toString().isEmpty() && !popupGroupGoal.getText().toString().isEmpty() && !popupGroupPlace.getText().toString().isEmpty() && !popupStartTimeInput.getText().toString().isEmpty() && !popupEndTimeInput.getText().toString().isEmpty() && pickedImgUri != null) {

                    // Get Groups's storage reference
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Groups");
                    // Store the inputs into storage on Firebase database
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownlaodLink = uri.toString();

                                    // Make the group object with user's inputs
                                    Group group = new Group(popupGroupName.getText().toString(),
                                            popupGroupGoal.getText().toString(),
                                            popupGroupPlace.getText().toString(),
                                            tutoring.toString(),
                                            popupNumOfGroupMembers.getText().toString(),
                                            popupStartTimeInput.getText().toString(),
                                            popupEndTimeInput.getText().toString(),
                                            imageDownlaodLink,
                                            currentUser.getUid(),
                                            currentUser.getPhotoUrl().toString());


                                    // Add the group
                                    addGroup(group);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    // Failure Case
                                    // Show error message
                                    showMessage(e.getMessage());
                                    // Back up the add-button and let the progress-button disappear
                                    popupClickProgress.setVisibility(View.INVISIBLE);
                                    popupAddBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                } else {
                    // If any inputs doesn't typed, print an error message
                    showMessage("Please verify all input fields and choose Group Image");
                    // Back up the add-button and let the progress-button disappear
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    // Add a group
    private void addGroup(Group group) {

        // Firebase
        // Get database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // get reference of the database
        DatabaseReference myRef = database.getReference("Groups").push();

        // Get the current user's Auth key
        String key = myRef.getKey();
        // Set up the groupKey with the user's key
        group.setGroupKey(key);

        // SuccessListener
        myRef.setValue(group).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // If a group is added successfully, print a success message
                showMessage("Group Added successfully");
                // Back up the add-button and let the progress-button disappear
                popupClickProgress.setVisibility(View.INVISIBLE);
                popupAddBtn.setVisibility(View.VISIBLE);
                // Dismiss the popup
                popAddGroup.dismiss();
            }
        });
    }

    // Print Message into user
    private void showMessage(String message) {
        // Long time showed up message
        Toast.makeText(Home.this, message, Toast.LENGTH_LONG).show();
    }

    // Back-button
    @Override
    public void onBackPressed() {

        // Get the drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // If the drawer showed up, close the drawer
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // Otherwise, go back
            super.onBackPressed();
        }
    }



    // Home menu in appBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    // Home menu option
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Get menu item id
        int id = item.getItemId();

        // If the setting menu clicked,
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Drawer
    // Navigation
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Get item id
        int id = item.getItemId();

        // If each section is clicked, replace the transaction into the section's fragment
        if (id == R.id.nav_home) {
            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
        } else if (id == R.id.nav_tutor) {
            getSupportActionBar().setTitle("Tutoring");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new TutorFragment()).commit();
        } else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("Profile");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
        } else if (id == R.id.nav_settings) {
            getSupportActionBar().setTitle("Settings");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
        } else if (id == R.id.nav_signout) {
            // If the user want to sign out, sign out through Firebase authorization
            FirebaseAuth.getInstance().signOut();
            mAuth.signOut();
            // Get intent
            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            // Go to login Activity
            startActivity(loginActivity);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Close the drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Update Navigation
    // It contains the current user info and navigation sections
    public void updateNavHeader() {

        // Set up the navigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        // Show the user info on drawer
        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_email);
        ImageView navUserPhoto = headerView.findViewById(R.id.nav_user_photo);

        navUserMail.setText(currentUser.getEmail());
        navUsername.setText(currentUser.getDisplayName());

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhoto);
    }
}