package com.example.myapplication.data.model;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.myapplication.BR;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

// We extend BaseObservable to set up data binding between
// the ScoRecord model and the UI. We'll achive this
// by decorating getters with the @Bindable attribute
// and using the "notifyPropertyChanged" method as
// part of the setter.
public class ScoRecord extends BaseObservable {

    @SerializedName("id")
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

    public String getScoID() { return objectID; }

    public String getScoName() { return name; }

    public String getVanityName() { return vanityName; }

    public String getScoAuthor() { return scoAuthor; }

    @Bindable
    public boolean isScoDownloaded() { return isDownloaded; }

    public void setIsScoDownloaded(Context context) {

        //  Retrieve the storage location of the app in external memory.
        File appRootExtDir = context.getExternalFilesDir(null);
        File scoFilePath = new File(appRootExtDir.getPath() + '/' + getScoID());

        //  Check to see if the directory has been created and notify the UI
        //  of the result.
        this.isDownloaded = scoFilePath.exists();
        notifyPropertyChanged(BR.scoDownloaded);
    }
}
