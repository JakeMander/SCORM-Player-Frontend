package com.example.myapplication.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScoRecord {

    @SerializedName("_id")
    @Expose
    private String objectID;

    @SerializedName("vanityName")
    @Expose
    private String vanityName;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("scoAuthor")
    @Expose
    private String scoAuthor;

    private boolean isDownloaded;

    public ScoRecord(String objectID, String scoName,
                     String vanityName, String scoAuthor)
    {
        this.objectID = objectID;
        this.name = scoName;
        this.vanityName = vanityName;
        this.scoAuthor = scoAuthor;
        this.isDownloaded = false;
    }

    public String getObjectID() { return objectID; }

    public String getScoName() { return name; }

    public String getVanityName() { return vanityName; }

    public String getScoAuthor() { return scoAuthor; }

    public boolean isScoDownloaded() {return isDownloaded; }
}
