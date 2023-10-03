package com.vishnu.digilocker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vishnu Panchal on 03-10-2023.
 */
public class MyAdapter extends RecyclerView.Adapter<MyViewholder> {
    // Declaring all the variables
    DownloadFilesActivity downloadFiles;
    ArrayList<DownModel> downModels;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();


    // Creating a constructor for the MyAdapter class
    public MyAdapter(DownloadFilesActivity downloadFiles, ArrayList<DownModel> downModels) {
        this.downloadFiles = downloadFiles;
        this.downModels = downModels;

    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Setting the layout for the recycler view
        LayoutInflater layoutInflater = LayoutInflater.from(downloadFiles.getBaseContext());
        View view = layoutInflater.inflate(R.layout.elements, null, false);

        return new MyViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, @SuppressLint("RecyclerView") int position) {
        // Setting the data to the recycler view
        holder.mName.setText(downModels.get(position).getName());
        holder.mLink.setText(downModels.get(position).getLink());

        // Checking the file extension and setting the image accordingly
        if (downModels.get(position).getName().endsWith(".ai") ){
            holder.mImage.setImageResource(R.drawable.aifile);
        } else if (downModels.get(position).getName().endsWith(".apk")) {
            holder.mImage.setImageResource(R.drawable.apkimage);
        } else if (downModels.get(position).getName().endsWith(".avi")) {
            holder.mImage.setImageResource(R.drawable.aviimage);
        }  else if (downModels.get(position).getName().endsWith(".css")) {
            holder.mImage.setImageResource(R.drawable.cssimage);
        } else if (downModels.get(position).getName().endsWith(".csv")) {
            holder.mImage.setImageResource(R.drawable.csvimage);
        } else if (downModels.get(position).getName().endsWith(".dbf")) {
            holder.mImage.setImageResource(R.drawable.dbfimage);
        } else if (downModels.get(position).getName().endsWith(".doc")) {
            holder.mImage.setImageResource(R.drawable.docimage);
        } else if (downModels.get(position).getName().endsWith(".docx")) {
            holder.mImage.setImageResource(R.drawable.docximage);
        } else if (downModels.get(position).getName().endsWith(".exe")) {
            holder.mImage.setImageResource(R.drawable.exeimage);
        } else if (downModels.get(position).getName().endsWith(".fla")) {
            holder.mImage.setImageResource(R.drawable.flaimage);
        } else if (downModels.get(position).getName().endsWith(".gif")) {
            holder.mImage.setImageResource(R.drawable.gifimage);
        } else if (downModels.get(position).getName().endsWith(".html")) {
            holder.mImage.setImageResource(R.drawable.htmlimage);
        } else if (downModels.get(position).getName().endsWith(".htm")) {
            holder.mImage.setImageResource(R.drawable.alldocimage);
        } else if (downModels.get(position).getName().endsWith(".jpeg")) {
            holder.mImage.setImageResource(R.drawable.gifimage);
        } else if (downModels.get(position).getName().endsWith(".jpg")) {
            holder.mImage.setImageResource(R.drawable.jpgimage);
        } else if (downModels.get(position).getName().endsWith(".mp3")) {
            holder.mImage.setImageResource(R.drawable.mpthreeimage);
        } else if (downModels.get(position).getName().endsWith(".mp4")) {
            holder.mImage.setImageResource(R.drawable.mpfourimage);
        } else if (downModels.get(position).getName().endsWith(".pdf")) {
            holder.mImage.setImageResource(R.drawable.pdfimage);
        } else if (downModels.get(position).getName().endsWith(".png")) {
            holder.mImage.setImageResource(R.drawable.pngimage);
        } else if (downModels.get(position).getName().endsWith(".ppt")) {
            holder.mImage.setImageResource(R.drawable.pptimage);
        } else if (downModels.get(position).getName().endsWith(".pptx")) {
            holder.mImage.setImageResource(R.drawable.alldocimage);
        } else if (downModels.get(position).getName().endsWith(".ods")) {
            holder.mImage.setImageResource(R.drawable.alldocimage);
        } else if (downModels.get(position).getName().endsWith(".odt")) {
            holder.mImage.setImageResource(R.drawable.alldocimage);
        } else if (downModels.get(position).getName().endsWith(".svg")) {
            holder.mImage.setImageResource(R.drawable.svgimage);
        } else if (downModels.get(position).getName().endsWith(".txt")) {
            holder.mImage.setImageResource(R.drawable.txtimage);
        } else if (downModels.get(position).getName().endsWith(".xls")) {
            holder.mImage.setImageResource(R.drawable.alldocimage);
        } else if (downModels.get(position).getName().endsWith(".xlsx")) {
            holder.mImage.setImageResource(R.drawable.alldocimage);
        } else if (downModels.get(position).getName().endsWith(".xml")) {
            holder.mImage.setImageResource(R.drawable.xmlimage);
        } else if (downModels.get(position).getName().endsWith(".zip")) {
            holder.mImage.setImageResource(R.drawable.zipimage);
        } else {
            holder.mImage.setImageResource(R.drawable.ic_launcher_background);
        }

        // Setting the click listener for the download button
        holder.mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference riversRef = storageReference.child(fAuth.getCurrentUser().getEmail()).child(downModels.get(position).getName());

                Task<Uri> url = riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    public void onSuccess(Uri uri) {

                        // Downloading the file
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        request.setTitle(downModels.get(position).getName());
                        request.setDescription("Downloading file...");

                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downModels.get(position).getName());

                        DownloadManager manager = (DownloadManager) downloadFiles.getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);

                        return;

                    }
                });

            }
        });

        holder.mDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Create a dialog box to confirm the deletion

                Log.d("TAG", "Delete");

                AlertDialog.Builder builder = new AlertDialog.Builder(downloadFiles);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete this file?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StorageReference riversRef = storageReference.child(fAuth.getCurrentUser().getEmail()).child(downModels.get(position).getName());
                        riversRef.delete();

                        FirebaseFirestore.getInstance().collection(fAuth.getCurrentUser().getEmail())
                                .whereEqualTo("name", downModels.get(position).getName())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                                        for (DocumentSnapshot snapshot : snapshotList) {
                                            FirebaseFirestore.getInstance().collection(fAuth.getCurrentUser().getEmail()).document(snapshot.getId()).delete();
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(downloadFiles, "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        holder.itemView.setVisibility(View.INVISIBLE);


                    }
                });


                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }

                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }









//                Log.d("TAG","Delete");
//
//                StorageReference riversRef = storageReference.child(fAuth.getCurrentUser().getEmail()).child(downModels.get(i).getName());
//                riversRef.delete();
//
//                FirebaseFirestore.getInstance().collection(fAuth.getCurrentUser().getEmail())
//                        .whereEqualTo("name",downModels.get(i).getName())
//                        .get()
//                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                            @Override
//                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//
//                                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
//                                for(DocumentSnapshot snapshot: snapshotList){
//                                    FirebaseFirestore.getInstance().collection(fAuth.getCurrentUser().getEmail()).document(snapshot.getId()).delete();
//                                }
//
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//
//                            }
//                        });
//
//                myViewHolder.itemView.setVisibility(View.INVISIBLE);
//
//            }
//        });
//
//
//    }

    public void downloadFile(Context context, String fileName, String destinationDirectory, String url) {

        File file = new File(Environment.getExternalStorageDirectory(),"Download/"+fileName);
        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri)

                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationUri(Uri.fromFile(file));
        downloadmanager.enqueue(request);
    }

    @Override
    public int getItemCount() {
        return downModels.size();
    }
}
