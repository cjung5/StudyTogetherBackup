package com.studytogether.studytogether.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.studytogether.studytogether.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

// Register Activity
public class RegisterActivity extends AppCompatActivity {

    // Flags
    static int PReqCode = 1 ;
    static int REQUESCODE = 1 ;


    // Items
    ImageView ImgUserPhoto;
    Uri pickedImgUri ;
    private EditText userEmail,userPassword,userPAssword2,userName;
    private ProgressBar loadingProgress;
    private Button regBtn;

    // Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content as activity_register
        setContentView(R.layout.activity_register);

        // Initialize the views
        userEmail = findViewById(R.id.regEmail);
        userPassword = findViewById(R.id.regPassword);
        userPAssword2 = findViewById(R.id.regPassword2);
        userName = findViewById(R.id.regName);
        loadingProgress = findViewById(R.id.regProgressBar);
        regBtn = findViewById(R.id.regBtn);
        loadingProgress.setVisibility(View.INVISIBLE);

        // Get Firebase authorization instance
        mAuth = FirebaseAuth.getInstance();

        // Listener for register-button
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If the user pressed the register-button, let the login-button disappear and show up the progress-button
                regBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);

                // Fill the inputs by a user
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPAssword2.getText().toString();
                final String name = userName.getText().toString();

                // If any inputs don't typed, show an error message to the user for recognizing reasons
                if( email.isEmpty() || name.isEmpty() || password.isEmpty()  || !password.equals(password2)) {
                    showMessage("Please Verify all fields") ;
                    // Back up the register
                    // -button and let the progress-button disappear
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                } else if (!email.contains("@mail.csuchico.edu")) {
                    showMessage("Please use the school mail as @mail.csuchico.edu") ;
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }
                else {
                    // Create a user accout
                    CreateUserAccount(email,name,password);
                }
            }
        });

        ImgUserPhoto = findViewById(R.id.regUserPhoto) ;

        // Lister for user Image
        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Limitation of SDK version
                if (Build.VERSION.SDK_INT >= 22) {
                    // Check permission
                    checkAndRequestForPermission();
                }
                else {
                    // Open gallery
                    openGallery();
                }
            }
        });
    }

    // Create user account
    private void CreateUserAccount(String email, final String name, String password) {

        // Store the user account into Firebase authorization
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If the work is successful,
                        if (task.isSuccessful()) {
                            // Show a success message
                            showMessage("Account created");
                            // update user information as a current user
                            updateUserInfo( name ,pickedImgUri,mAuth.getCurrentUser());

                        }
                        else {
                            // The work is failed, Show an error message
                            showMessage("account creation failed" + task.getException().getMessage());
                            // Back up the register-button and let the progress-button disappear
                            regBtn.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }


    // update user photo and name
    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

        // Get storage reference from Firebase
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(uri).build();

                        // Update the user
                        currentUser.updateProfile(profleUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            showMessage("Register Complete");
                                            // Go to Home activity
                                            updateUI();
                                        }

                                    }
                                });
                    }
                });
            }
        });

    }

    // Update UI: replace the activity into Home activity
    private void updateUI() {

        // Get Home intent
        Intent homeActivity = new Intent(getApplicationContext(),Home.class);
        // Go to Home activity
        startActivity(homeActivity);
        // Finish this activity
        finish();

    }

    // Show messages to the user
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    // Open gallery
    private void openGallery() {
        // Get intent
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        // Set up the intent type
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    // Check the permission
    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegisterActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();
            }
            else {
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }
        else
            // Open gallery
            openGallery();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {
            pickedImgUri = data.getData() ;
            ImgUserPhoto.setImageURI(pickedImgUri);
        }
    }
}