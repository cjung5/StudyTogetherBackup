package com.studytogether.studytogether.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;
import com.studytogether.studytogether.R;


// Login Activity
public class LoginActivity extends AppCompatActivity {


    static final int GOOGLE_SIGN_IN = 123;

    // Items
    private Button btnGoogleLogin;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;
    private Intent HomeActivity;
    private ImageView loginPhoto;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content with activity_login layout
        setContentView(R.layout.activity_login);

        // Hide appBar
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();

        // Set up items
        loginProgress = findViewById(R.id.login_progress);
        btnGoogleLogin = findViewById(R.id.google_login_btn);

        // Firebase authorization
        mAuth = FirebaseAuth.getInstance();

        // Get Home intent
        HomeActivity = new Intent(this,com.studytogether.studytogether.Activities.Home.class);

        loginPhoto = findViewById(R.id.login_photo);
        loginPhoto.setImageResource(R.drawable.logo);

        // Set the progress-button as invisible mode
        loginProgress.setVisibility(View.INVISIBLE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("google login btn clicked");
                SignInGoogle();
            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showMessage("firebaseAuthWithGoogle");

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        String userEmail = user.getEmail();
                        if(userEmail.contains("@mail.csuchico.edu")) {
                            loginProgress.setVisibility(View.INVISIBLE);
                            updateUIGoogle(user);
                        } else {
                            showMessage("Please sign in with school email");
                            FirebaseAuth.getInstance().signOut();
                            mAuth.signOut();

                            // Google sign out
                            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                                    new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            updateUIGoogle(null);
                                        }
                                    });
                            //SignInGoogle();
                        }

                    } else {
                        showMessage("Could not log in");
                        loginProgress.setVisibility(View.INVISIBLE);
                        updateUIGoogle(null);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                showMessage("error");
            }
        }
    }

    public void SignInGoogle() {
        loginProgress.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    private void updateUIGoogle(FirebaseUser user) {
        if (user != null) {

            //String photo = String.valueOf(user.getPhotoUrl());
            //Picasso.with(LoginActivity.this).load(photo).into(loginPhoto);

            startActivity(HomeActivity);
            finish();
        } else {
            showMessage("Could not log in");
            //SignInGoogle();
        }
    }

    // Show message to the user
    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if( currentUser != null) {
            updateUIGoogle(currentUser);
        } else {
            showMessage("Please sign-in");
            //SignInGoogle();
        }
    }
}