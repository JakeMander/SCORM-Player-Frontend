package com.example.myapplication.data.model;

import java.util.List;

/**
 *  POJO Class to represent an organization of
 *  SCO's within the manifest.
 *
 *  Used to define the resources and assets
 *  required for a particular unit of learning.
 *
 *  Multiple organizations can be used to vary
 *  the structure and ordering of
 */
public class ManifestOrganization {
    private String mIdentifier;
    private String mTitle;
    private List<ManifestItem> mItems;

    public ManifestOrganization(String identifier, String title,
                                List<ManifestItem> items) {
        this.mIdentifier = identifier;
        this.mTitle = title;
        this.mItems = items;
    }

    public String getIdentifier() { return mIdentifier; }
    public String getTitle() { return mTitle; }
    public List<ManifestItem> getNestedItems() {return mItems; }
}
