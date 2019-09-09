package com.example.kafeinevents.api;

public class ApiUtils {

    public static final String BASE_URL = "http://207.154.232.181:8080/portal-api/auth-server/";

    public static UserService getUserService(){
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

}
