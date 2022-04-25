package com.example.myapplication.data.model;

import java.util.List;

/**
 *  POJO class to represent a nested item
 *  within a manifest file's organization.
 *
 *  Used to identify a resource which is bound
 *  to a particular organization.
 */
public class ManifestItem {
    private String mTitle;
    private String mParameters;
    // Used to link an organization item to a particular launchable resource.
    private String mIdentifierRef;
    private List<ManifestItem> mChildItems;

    public ManifestItem (String title, String parameters,
                         String identifierRef,
                         List<ManifestItem> childItems) {
        this.mTitle = title;
        this.mParameters = parameters;
        this.mIdentifierRef = identifierRef;
        this.mChildItems = childItems;
    }

    public String getTitle() { return mTitle; }
    public String getParameters() { return mParameters; }
    public String getIdentifierRef() { return mIdentifierRef; }
    public List<ManifestItem> getChildItems() {return mChildItems; }
 }
