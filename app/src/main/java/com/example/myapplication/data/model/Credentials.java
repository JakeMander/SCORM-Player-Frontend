package com.example.myapplication.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Credentials {

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("passwordHash")
    @Expose
    private String passwordHash;

    public Credentials(String username, String passwordHash)
    {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String GetUsername()
    {
        return username;
    }

    public String GetPasswordHash()
    {
        return passwordHash;
    }
}
