package com.vishnu.digilocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DownloadFilesActivity extends AppCompatActivity {
    // Creating an object for FirebaseFirestore
    FirebaseFirestore db;

    // Declaring all the variables
    RecyclerView mRecyclerView;
    ArrayList<DownModel> downModelArrayList = new ArrayList<>();
    MyAdapter myAdapter;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_files);
        // Calling the methods to set up the RecyclerView and Firebase
        setUpRV();
        setUpFB();

        // Calling the method to get the data from Firebase
        dataFromFirebase();

    }

    // Creating a method to get the data from Firebase
    private void dataFromFirebase() {

        // Checking if the ArrayList is empty or not
        if(downModelArrayList.size()>0)
            downModelArrayList.clear();

        // Getting the data from Firebase
        // If the data is retrieved successfully, then the data is added to the ArrayList
        db.collection(fAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for(DocumentSnapshot documentSnapshot: task.getResult()) {

                            DownModel downModel= new DownModel(documentSnapshot.getString("name"),
                                    documentSnapshot.getString("link"));
                            downModelArrayList.add(downModel);

                        }

                        // Setting the adapter to the RecyclerView
                        myAdapter= new MyAdapter(DownloadFilesActivity.this,downModelArrayList);
                        mRecyclerView.setAdapter(myAdapter);
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DownloadFilesActivity.this, "Error ;-.-;", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // Creating a method to set up the Firebase
    private void setUpFB(){
        db=FirebaseFirestore.getInstance();
    }

    // Creating a method to set up the RecyclerView
    private void setUpRV(){
        mRecyclerView= findViewById(R.id.recycle);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Creating a method to go to the Downloads page
    public void goToDownloads(View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }


}