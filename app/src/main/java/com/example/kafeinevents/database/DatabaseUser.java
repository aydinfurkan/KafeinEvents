package com.example.kafeinevents.database;

import androidx.annotation.NonNull;

import com.example.kafeinevents.data.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUser {

    private DatabaseReference mDatabase;

    public DatabaseUser(){
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    //UserModel listener
    public interface OngetUserModel{
        void successful(UserModel userModel);
        void cancelled(String exception);
    }

    public void readUserModel(String userId, final OngetUserModel ongetUserModel) {

        mDatabase.child(userId).child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                ongetUserModel.successful(userModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ongetUserModel.cancelled("Failed to read user.");
            }
        });

    }

    // PEvents Listener
    public interface OngetParticipatedList{
        void successful(List<String> participatedList);
        void cancelled(String exception);
    }

    public void getParticipatedList(String userId, final OngetParticipatedList ongetParticipatedList){

        mDatabase.child(userId).child("participated").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<String> participatedList = new ArrayList<>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    String eventId = postSnapshot.getKey();
                    participatedList.add(eventId);
                }

                ongetParticipatedList.successful(participatedList);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ongetParticipatedList.cancelled("Failed to read participated events.");
            }
        });

    }

    // User Listener
    public interface OngetUser{
        void successful(List<String> cEventIds, List<String> pEventIds, UserModel userModel);
        void cancelled(String exception);
    }

    public void readUser(String userId, final OngetUser ongetUser){

        mDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> cEventIds = new ArrayList<>();
                List<String> pEventIds = new ArrayList<>();
                UserModel userModel = new UserModel();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    if (postSnapshot.getKey() == null){
                        return;
                    }

                    switch (postSnapshot.getKey()){

                        case "created":
                            for (DataSnapshot costSnapshot : postSnapshot.getChildren()){
                                cEventIds.add(costSnapshot.getKey());
                            }
                            break;
                        case "participated":
                            for (DataSnapshot costSnapshot : postSnapshot.getChildren()){
                                pEventIds.add(costSnapshot.getKey());
                            }
                            break;
                        case "profile":
                            userModel = postSnapshot.getValue(UserModel.class);
                            break;
                    }

                }

                ongetUser.successful(cEventIds, pEventIds, userModel);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ongetUser.cancelled("Failed to read user profile");
            }
        });


    }

    // Is User Exists
    public interface OncheckUserExists{
        void exist(String userId);
        void notExist();
    }

    public void isUserExists(final String email,final OncheckUserExists checkUserExists) {

        mDatabase.child("IDs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    if( email.equals(postSnapshot.getValue())){
                        String userId = postSnapshot.getKey();
                        checkUserExists.exist(userId);
                        return;
                    }
                }

                checkUserExists.notExist();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Create User
    public String createNewUser(UserModel userModel) {

        String userId = mDatabase.push().getKey();
        userModel.setUserId(userId);
        mDatabase.child("IDs").child(userId).setValue(userModel.getEmail());
        mDatabase.child(userId).child("profile").setValue(userModel);

        return userId;
    }

    public void setParticipated(String userId, String eventId, boolean isParticipated){
        if(isParticipated){
            mDatabase.child(userId).child("participated").child(eventId).setValue(true);
        }else{
            mDatabase.child(userId).child("participated").child(eventId).removeValue();
        }
    }

    public void setCreated(String userId, String eventId, boolean isCreated){
        if(isCreated){
            mDatabase.child(userId).child("created").child(eventId).setValue(true);
        }else{
            mDatabase.child(userId).child("created").child(eventId).removeValue();
        }
    }

    public void setProfile(String userId, UserModel userModel){
        mDatabase.child(userId).child("profile").setValue(userModel);

    }
}
