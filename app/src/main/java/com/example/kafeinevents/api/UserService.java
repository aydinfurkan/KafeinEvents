package com.example.kafeinevents.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UserService {

    @POST("oauth/token")
    @Headers({
            "Authorization: Basic a2JpX2NsaWVudDprYmlfc2VjcmV0"
    })
    @FormUrlEncoded
    Call<ResObj> login(@Field("username") String username,
                       @Field("password") String password,
                       @Field("grant_type") String grant_type);
}