package com.example.myapplication.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("accessToken")
    @Expose
    private String authToken;

    public LoggedInUser(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthenticationToken() {
        return authToken;
    }
}