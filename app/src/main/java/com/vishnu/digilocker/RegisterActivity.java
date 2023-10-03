package com.vishnu.digilocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    // Declaring all the variables
    EditText mFullname, mEmail, mPassword;
    Button mRegistrationBtn;
    TextView mLoginBtn;

    // Creating an object for FirebaseAuth
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    // Creating an object for FirebaseFirestore
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Assigning the variables to the respective views
        mFullname = findViewById(R.id.username);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRegistrationBtn = findViewById(R.id.registerBtn);
        mLoginBtn = findViewById(R.id.loginDirect);
        progressBar = findViewById(R.id.progressBar);

        // Creating an instance for FirebaseAuth
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Setting an onclick listener for the register button
        mRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Getting the values from the edit text
                final String email = mEmail.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();
                final String name = mFullname.getText().toString().trim();

                // Checking if the email and password fields are empty
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required!");
                    return;
                }

                // Check if password is at least 8 characters
                if(password.length()<8){
                    mPassword.setError("Password should be atleast 8 characters");
                    return;
                }


                // Check if email is valid
//                if (!email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")) {
//                    mEmail.setError("Email is invalid!");
//                    return;
//                }
//
//                // Check if password has at least 8 characters, at least one letter and one number
//                if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
//                    mPassword.setError("Password is invalid!");
//                    return;
//                }

                // Setting the progress bar to visible
                progressBar.setVisibility(view.VISIBLE);

                // Creating a user with email and password
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser fuser = fAuth.getCurrentUser();

                            // Sending a verification email to the user if the user is created successfully
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "onFailure: Email not sent " + e.getMessage());
                                }
                            });

                            // Displaying a toast message
                            Toast.makeText(RegisterActivity.this,"user created",Toast.LENGTH_SHORT).show();

                            // Creating a hashmap for storing the user data
                            Map<String,Object> user = new HashMap<>();
                            user.put("Name",name);
                            user.put("Email",email);
                            user.put("User ID",fAuth.getCurrentUser().getUid());
                            user.put("Password",password);

                            // Creating a document reference for storing the user data in the database
                            DocumentReference documentReference = db.collection("User's Data").document(fAuth.getCurrentUser().getUid());
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG","OnSuccess: user Profile is created for "+fAuth.getCurrentUser().getUid());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("TAG","Error: user Profile is not created for "+fAuth.getCurrentUser().getUid());
                                }
                            });

                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        }
                        else{
                            Toast.makeText(RegisterActivity.this,"Error! "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        // Setting an onclick listener for the login button
        // Redirecting the user to the login page
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
    }
}