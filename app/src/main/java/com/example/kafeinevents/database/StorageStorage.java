package com.example.kafeinevents.database;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StorageStorage {

    private StorageReference mStorage;

    private int successCount = 0;
    private int cancelCount = 0;

    public StorageStorage(){
        mStorage = FirebaseStorage.getInstance().getReference("storage");
    }

    public interface OngetImages{
        void successful(List<File> images, int successCount, int cancelCount);
        void cancelled(String exception);
    }

    public void readImages(String eventId, final OngetImages ongetImages){

        final List<File> images = new ArrayList<>();

        mStorage.child(eventId).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {

                final int imageNumber = listResult.getItems().size();

                for (StorageReference item : listResult.getItems()){
                    try {
                        final File localFile = File.createTempFile("storage", "jpg");
                        images.add(localFile);
                        item.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                isFinish(imageNumber, true, ongetImages, images);
                            }
                        }).addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                isFinish(imageNumber, false, ongetImages, images);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                ongetImages.cancelled("Failed to read images.");
            }
        });
    }

    private void isFinish(int imageNumber, boolean isSuccess, OngetImages ongetImages, List<File> images){
        if(isSuccess)
            successCount++;
        else
            cancelCount++;

        if(imageNumber == successCount + cancelCount)
            ongetImages.successful(images, successCount , cancelCount);

    }

    public interface OnsetImages{
        void successful(int successCount, int cancelCount);
        void progress(int progress);
    }

    public void writeImages(final List<Uri> images, String eventId, final OnsetImages onsetImages){

        for(Uri image : images){
            String push = UUID.randomUUID().toString();
            mStorage.child(eventId).child(push + ".jpg").putFile(image)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    isFinish(images.size(), true, onsetImages);
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    isFinish(images.size(), false, onsetImages);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 / images.size()) * (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    onsetImages.progress((int) progress);
                }
            });

        }

    }

    private void isFinish(int imageNumber, boolean isSuccess, OnsetImages onsetImages){
        if(isSuccess)
            successCount++;
        else
            cancelCount++;

        if(imageNumber == successCount + cancelCount)
            onsetImages.successful(successCount , cancelCount);

    }

    public void downloadImage(){}

}
