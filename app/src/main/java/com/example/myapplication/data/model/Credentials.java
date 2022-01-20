package com.example.myapplication.data.model;

public class Credentials {

    private String username;
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
