package com.vishnu.digilocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    // Declaring all the variables
    EditText mEmail,mPassword;
    Button mLoginBtn;
    TextView mCreateBtn,forpass;
    ProgressBar progressBar;

    // Creating an object for FirebaseAuth
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Assigning the variables to the respective views
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = findViewById(R.id.registerDirect);
        forpass = findViewById(R.id.forgetPassword);

        // Setting an onclick listener for the login button
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Getting the values from the edit text
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                // Checking if the email and password fields are empty
                // If empty, then display an error message
                if(TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }
                if(TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required.");
                    return;
                }
                if(password.length() < 8) {
                    mPassword.setError("Password Must have 8 Characters");
                    return;
                }

                // Setting the progress bar to visible
                progressBar.setVisibility(View.VISIBLE);

                // Authenticating the user using the email and password
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Checking if the task is successful
                        if(task.isSuccessful()) {

                            // Checking if the user has verified the email
                            if(fAuth.getCurrentUser().isEmailVerified()){
                                Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }else{
                                Toast.makeText(LoginActivity.this, "Verify E-Mail first", Toast.LENGTH_SHORT).show();

                            }

                        } else{
                            // Displaying an error message if the task is not successful
                            Toast.makeText(LoginActivity.this, "Please first Sign-Up"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });


            }
        });

        // Setting an onclick listener for the forget password text view
        forpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Getting the value from the email edit text
                String email = mEmail.getText().toString().trim();
                if(email.isEmpty()){

                    // Displaying an error message if the email field is empty
                    mEmail.setError("Email Required");
                }
                else {

                    // Sending the password reset link to the email
                    fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Password reset link send", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Password reset fails", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        // Setting an onclick listener for the create account text view
        // If the user clicks on the text view, then the user will be redirected to the Register activity
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });

    }
}