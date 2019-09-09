package com.example.kafeinevents.database;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class StorageEvent {

    private StorageReference mStorage;

    public StorageEvent(){
        mStorage = FirebaseStorage.getInstance().getReference("events");
    }

    public interface OngetImage{
        void successful(File file);
        void cancelled(String exception);
    }

    public void readImage(String eventId, final OngetImage ongetImage){

        try {
            final File localFile = File.createTempFile("eventImage", "jpg");
            mStorage.child(eventId + ".jpg").getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            ongetImage.successful(localFile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ongetImage.cancelled("Failed to read image.");
                        }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public interface OnsetImage{
        void successful();
        void cancelled(String exception);
        void progress(int progress);
    }

    public void writeImage(Uri image, String eventId, final OnsetImage onsetImage){

        mStorage.child(eventId + ".jpg").putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        onsetImage.successful();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onsetImage.cancelled("Failed to write image.");
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = 100.0 * (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        onsetImage.progress((int) progress);
                    }
        });
    }

    public void deleteImage(String eventId){
        mStorage.child(eventId + ".jpg").delete();
    }

}
