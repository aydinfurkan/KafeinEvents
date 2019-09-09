package com.example.kafeinevents.data.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kafeinevents.R;
import com.example.kafeinevents.data.model.UserModel;

import java.util.List;

public class AttendeesAdapter extends BaseAdapter {

    private LayoutInflater userInflater;
    private List<UserModel> userModels;

    public AttendeesAdapter(Activity activity, List<UserModel> userModels){

        userInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.userModels = userModels;

    }

    @Override
    public int getCount() {
        return userModels.size();
    }

    @Override
    public Object getItem(int i) {
        return userModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v;
        v = userInflater.inflate(R.layout.attendeeslist_item, null);

        TextView name = v.findViewById(R.id.attendeeslist_itemName);
        ImageView image = v.findViewById(R.id.attendeeslist_itemImage);

        initializeImage(image);

        UserModel userModel = userModels.get(i);

        name.setText(userModel.getName());

        return v;
    }

    private void initializeImage(ImageView image) {



    }
}
