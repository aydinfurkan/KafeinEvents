package com.example.kafeinevents.data;

import com.example.kafeinevents.data.model.UserModel;

public class User {

    private UserModel user;

    /*public User(String email){

        String s = email.substring(0, email.indexOf("@"));

        userId = s.replace(".", "_");

        if(s.contains(".")){
            String[] parse = s.split("\\.");

            String n = "";

            for(String i : parse){
                n += i.substring(0, 1).toUpperCase() + i.substring(1) + " ";
            }

            name = n.trim();
        }
        else{
            name = userId.substring(0, 1).toUpperCase() + userId.substring(1);
        }

    }

    public User(UserModel user) {
        this.user = user;
    }

    protected User(Parcel in) {
        userId = in.readString();
        name = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(name);
    }*/
}
