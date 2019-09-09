package com.example.kafeinevents.data.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kafeinevents.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class StorageAdapter extends RecyclerView.Adapter<StorageAdapter.MyViewHolder> {

    private LayoutInflater userInflater;
    private List<File> images;
    private List<Boolean> checkBoxList;

    public StorageAdapter(Context context, List<File> images, List<Boolean> checkBoxList){
        this.images = images;
        this.checkBoxList = checkBoxList;
        userInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public StorageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = userInflater.inflate(R.layout.storage_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {
        File imageFile = images.get(position);

        Picasso.with(myViewHolder.v.getContext()).load(imageFile).resize(500,500).into(myViewHolder.image);

        myViewHolder.checkBox.setOnCheckedChangeListener(null);

        myViewHolder.checkBox.setChecked(checkBoxList.get(position));

        myViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(TAG, "SET -> " + myViewHolder.getAdapterPosition() + " --> " + b);
                checkBoxList.set(myViewHolder.getAdapterPosition(), b);
            }
        });
    }


    @Override
    public int getItemCount() {
        return images.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        protected ImageView image;
        protected CheckBox checkBox;
        View v;

        public MyViewHolder(@NonNull View v) {
            super(v);
            this.v = v;
            image = v.findViewById(R.id.storage_itemImage);
            checkBox = v.findViewById(R.id.storage_itemCheckBox);
        }

    }

}
