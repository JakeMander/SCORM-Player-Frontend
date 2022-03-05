package com.example.myapplication.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DownloadedSco {
    @SerializedName("scoId")
    @Expose
    private String scoId;

    @SerializedName("scoData")
    @Expose
    private String scoData;

    public DownloadedSco(String id, String data) {
        this.scoId = id;
        this.scoData = data;
    }

    public String getScoId() { return this.scoId; }

    public String getScoData() { return  this.scoData; }
}
