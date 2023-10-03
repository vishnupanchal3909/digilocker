package com.vishnu.digilocker;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 234;
    private static final int PERMISSION_REQUEST_CODE =200;

    // Creating variables for the buttons and imageview
    private Button buttonChoose;
    private Button buttonUpload;
    private ImageView imageView;

    // Creating a variable for the Uri and cardview
    private Uri filePath;
    private CardView cardView;

    // Creating a variable for the Firebase Storage Reference and FirebaseAuth
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assigning the variables to the respective views
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        cardView = (CardView) findViewById(R.id.cardView);
        imageView = (ImageView) findViewById(R.id.imageView);

        // Setting the onclick listener for the buttons
        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);

        // Setting the visibility of the imageview
        imageView.setVisibility(View.VISIBLE);

        // Creating an instance for FirebaseAuth
        fAuth = FirebaseAuth.getInstance();
        Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_launcher);
        imageView.setImageDrawable(myDrawable);

        // Checking if the user is logged in or not
        // If the user is not logged in then the user will be redirected to the login page
        if (fAuth.getCurrentUser() == null || (!fAuth.getCurrentUser().isEmailVerified())) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else {
            if(checkPermission()){
                Toast.makeText(this, "Permission grant", Toast.LENGTH_SHORT).show();
            }else {
                requestPermission();
            }
            // If the user is logged and email is verified then it will ask for the permission
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            if (notificationManager != null && notificationManager.getNotificationChannel(CHANNEL_ID)==null){
//                NotificationChannel notificationChannel = new NotificationChannel(
//                        CHANNEL_ID,
//                        "PDF downloaded",
//                        NotificationManager.IMPORTANCE_HIGH
//                        ;
//                notificationChannel.setDescription("This channel is used for pdf downloaded";
//                notificationManager.createNotificationChannel(notificationChannel);
//
//            }
//        }
//
//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
//        managerCompat.notify(404,builder.build());

    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
//                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                    Log.d("WRITE PERMISSION", "onRequestPermissionsResult: true");
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    requestPermission();

                }
            }
        }
    }
    // Creating a method for the permission
//    private void askPermission() {
//
////        // Creating a permission listener
//        PermissionListener permissionListener = new PermissionListener() {
//
//            @Override
//            public void onPermissionGranted() {
//            }
//
//            @Override
//            public void onPermissionDenied(List<String> deniedPermissions) {
//            }
//        };
//
//        // Asking for the permission
//        TedPermission.with(MainActivity.this)
//                .setPermissionListener(permissionListener)
//                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .check();
//
//    }

    // Creating a method for the file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String retrieveFileName = null;

        // Checking if the request code is the same as what is passed here it is 234
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            retrieveFileName = data.getData().getLastPathSegment().toLowerCase();


            // Setting the image to the imageview based on the file type
            if (retrieveFileName.contains("image")) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (retrieveFileName.contains(".ai")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);
            } else if (retrieveFileName.contains(".apk")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".avi")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".css")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".csv")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".dbf")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".doc")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".exe")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".fla")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".gif")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".htm")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".html")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".jpg")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".json")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".htm")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".mp3")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".mp4")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".ods")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".odt")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".pdf")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);
            } else if (retrieveFileName.contains(".png")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);

            } else if (retrieveFileName.contains(".ppt")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);
            } else if (retrieveFileName.contains(".pptx")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);
            } else if (retrieveFileName.contains(".svg")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);
            } else if (retrieveFileName.contains(".txt")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);
            } else if (retrieveFileName.contains(".xls")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);
            } else if (retrieveFileName.contains(".xlsx")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);
            } else if (retrieveFileName.contains(".xml")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);
            } else if (retrieveFileName.contains(".zip")) {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
                imageView.setImageDrawable(myDrawable);
            }
        } else {
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
            imageView.setImageDrawable(myDrawable);
        }

        // Setting the visibility of the imageview
        cardView.setVisibility(View.VISIBLE);

        // Setting the name of the file to be saved
        EditText choosenFileName = findViewById(R.id.fileNameToBeSaved);
        if (retrieveFileName.contains("image")) {
            choosenFileName.setText(retrieveFileName + ".jpg");
        } else {
            choosenFileName.setText(retrieveFileName);
        }
    }


    // Creating the method to upload the file
    private void uploadFile() {

        // Getting the name of the file to be saved
        EditText fileName = findViewById(R.id.fileNameToBeSaved);

        // Checking whether the file type is supported or not, depending on the file extension
        if (fileName.getText().toString().contains(".jpg") || fileName.getText().toString().contains(".mp3") || fileName.getText().toString().contains(".mp4")
                || fileName.getText().toString().contains(".pdf") || fileName.getText().toString().contains(".txt") || fileName.getText().toString().contains(".doc")
                || fileName.getText().toString().contains(".docx") || fileName.getText().toString().contains(".ppt") || fileName.getText().toString().contains(".pptx")
                || fileName.getText().toString().contains(".apk") || fileName.getText().toString().contains(".xls") || fileName.getText().toString().contains(".xlsx")
                || fileName.getText().toString().contains(".zip") || fileName.getText().toString().contains(".gif") || fileName.getText().toString().contains(".htm")
                || fileName.getText().toString().contains(".html") || fileName.getText().toString().contains(".jpeg") || fileName.getText().toString().contains(".jpg")
                || fileName.getText().toString().contains(".ods") || fileName.getText().toString().contains(".odt") || fileName.getText().toString().contains(".svg")
                || fileName.getText().toString().contains(".ai") || fileName.getText().toString().contains(".exe") || fileName.getText().toString().contains(".fla")
                || fileName.getText().toString().contains(".json") || fileName.getText().toString().contains(".xml") || fileName.getText().toString().contains(".png")
                || fileName.getText().toString().contains(".exe")) {


            // Checking whether the file is selected or not
            if (filePath != null) {
                // Code for showing progressDialog while uploading
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading");
                progressDialog.show();

                // Getting the name of the file to be saved
                String name = fileName.getText().toString().trim();
                StorageReference riversRef = storageReference.child(fAuth.getCurrentUser().getEmail()).child(name);

                // Adding addOnSuccessListener to second StorageReference
                riversRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // If the upload is successfull, hiding the progress dialog

                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        progressDialog.dismiss();
                                        // Displaying a success toast message
                                        Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                                        // Creating a map to store the name and link of the file
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("name", riversRef.getName());
                                        user.put("link", uri.toString());

                                        // Adding the map to the Firestore database
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        DocumentReference documentReference = db.collection(fAuth.getCurrentUser().getEmail()).document();
                                        documentReference.set(user);
                                    }
                                });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // If the upload is not successfull, hiding the progress dialog
                                progressDialog.dismiss();

                                // And displaying error message
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                // Calculating progress percentage
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                // Displaying percentage in progress dialog
                                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            }
                        });
            }
            // If the file is not selected
            else {
                // Displaying an error toast
                Toast.makeText(getApplicationContext(), "File not Selected", Toast.LENGTH_SHORT).show();
            }
        } else {
            fileName.setError("File Extension required!");
            return;
        }
    }

    // Creating the method for choose the file and uploading button
    @Override
    public void onClick(View view) {
        // If the clicked button is choose
        if (view == buttonChoose) {
            showFileChooser();
        }
        // If the clicked button is upload
        else if (view == buttonUpload) {
            uploadFile();
        } else {
            return;
        }
    }

    // Creating the Menu for the activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // Creating the switch case for the menu items
        switch (item.getItemId()) {

            // If the item is logout, then sign out the user and redirect to the login page
            case R.id.logout_id:
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                break;
            default:
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }

    // If user clicks on the View Uploads button, then redirect to the DownloadFiles activity
    public void showUploads(View view) {
        startActivity(new Intent(getApplicationContext(), DownloadFilesActivity.class));
        finish();
    }

}