package com.example.kafeinevents.data.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kafeinevents.R;
import com.example.kafeinevents.data.adapter.StorageAdapter;
import com.example.kafeinevents.data.photo.Gallery;
import com.example.kafeinevents.database.StorageStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StorageDialog extends Dialog {

    private static final int MULTIPLE_GALLERY_REQUEST = 2;
    private String eventId;
    private List<File> eventImages;
    private Activity activity;
    private StorageAdapter storageAdapter;
    private List<Boolean> checkBoxList = new ArrayList<>();

    public StorageDialog(@NonNull Context context, String eventId, Activity activity) {
        super(context);
        this.activity = activity;
        this.eventId = eventId;
        setContentView(R.layout.storage_layout);
    }

    @Override
    public void show() {
        super.show();
        initializeImages();
        initializeListeners();
    }

    private void initializeImages() {

        final RecyclerView recyclerView = findViewById(R.id.storage_layoutRecycleView);

        new StorageStorage().readImages(eventId, new StorageStorage.OngetImages() {

            @Override
            public void successful(List<File> images, int successCount, int cancelCount) {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                recyclerView.setHasFixedSize(true);

                for (int i = 0; i < images.size(); i++) {
                    checkBoxList.add(false);
                }

                storageAdapter = new StorageAdapter(getContext(), images, checkBoxList);
                recyclerView.setAdapter(storageAdapter);
                eventImages = images;
                Toast.makeText(getContext(), cancelCount + " resim okunamadı.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void cancelled(String exception) {
                Toast.makeText(activity, exception, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initializeListeners() {

        Button closeButton = findViewById(R.id.storage_layoutCloseButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button newButton = findViewById(R.id.storage_layoutAddButton);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Gallery(activity, true, MULTIPLE_GALLERY_REQUEST).open();
                dismiss();
            }
        });

        Button downloadButton = findViewById(R.id.storage_layoutDownloadButton);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0; i < checkBoxList.size(); i++){
                    if(checkBoxList.get(i)){
                        Toast.makeText(activity, eventImages.get(i).getName(), Toast.LENGTH_SHORT).show(); // TODO make image model for reach imageId
                    }

                }
            }
        });

    }

}
