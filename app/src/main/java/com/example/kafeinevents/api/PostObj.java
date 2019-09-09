package com.example.kafeinevents.api;

import com.google.gson.annotations.SerializedName;

public class PostObj {

    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("grant_type")
    private String grant_type;

}
