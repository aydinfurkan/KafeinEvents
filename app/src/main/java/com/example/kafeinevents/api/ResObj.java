package com.example.kafeinevents.api;

public class ResObj {

    private String access_token;
    private String token_type;
    private String expires_in;
    private String scope;
    private String organization;
    private String first_login;
    private String jti;


    public String getOrganization() {return organization;}
    public String getAccess_token() {return access_token;}

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
