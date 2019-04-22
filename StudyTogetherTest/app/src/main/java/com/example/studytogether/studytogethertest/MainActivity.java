package com.example.studytogether.studytogethertest;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.studytogether.studytogethertest.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {
    MaterialEditText editNewUser, editNewPassword, editNewEmail; // for sign up
    MaterialEditText editUser, editPassword; // for sign in

    Button btnSignUp, btnSignIn;

    FirebaseDatabase database;
    DatabaseReference users;
    //private FirebaseFirestore database = FirebaseFirestore.getInstance();
    //private CollectionReference userRef = database.collection("Users");

    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");


        editUser = (MaterialEditText)findViewById(R.id.editUser);
        editPassword = (MaterialEditText)findViewById(R.id.editPassword);

        btnSignIn = (Button)findViewById(R.id.btn_sign_in);
        btnSignUp = (Button)findViewById(R.id.btn_sign_up);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSingUpDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(editUser.getText().toString(), editPassword.getText().toString());
            }
        });
    }

    private void signIn(String user, String pwd) {
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(user).exists()) {
                    if(!user.isEmpty()) {
                        User login = dataSnapshot.child(user).getValue(User.class);
                        if(login.getPassword().equals(pwd))
                            //openHomeActivity();
                            Toast.makeText(MainActivity.this, "Login OK!!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Please enter your user name", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(MainActivity.this, "User is not exists!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showSingUpDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Sign Up");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View sign_up_layout = inflater.inflate(R.layout.sign_up_layout, null);

        editNewUser = (MaterialEditText)sign_up_layout.findViewById(R.id.editNewUserName);
        editNewPassword = (MaterialEditText)sign_up_layout.findViewById(R.id.editNewPassword);
        editNewEmail = (MaterialEditText)sign_up_layout.findViewById(R.id.editNewEmail);

        alertDialog.setView(sign_up_layout);
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);

        alertDialog.setNegativeButton("NO", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 final User user = new User(
                         editNewUser.getText().toString(),
                         editNewPassword.getText().toString(),
                         editNewEmail.getText().toString()
                 );

                 users.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         if(dataSnapshot.child(user.getUserName()).exists())
                             Toast.makeText(MainActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                         else {
                             users.child(user.getUserName()).setValue(user);
                             Toast.makeText(MainActivity.this, "User registration success!", Toast.LENGTH_SHORT).show();
                         }
                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });
                 dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }
    /*
    public void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    */

}
