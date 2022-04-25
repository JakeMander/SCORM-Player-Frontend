package com.example.myapplication.data.model;

import com.example.myapplication.data.ScoManifestRepository;

import java.util.List;

/**
 *  POJO Class to represent the manifest file of
 *  a SCO collection. Contains metadata to provide
 *  supplementary details, the organizations to detail
 *  how the content contained in the ZIP is structured
 *  and organized.
 */
public class ScoManifest {

    private ManifestMetadata mMetaData;
    private List<ManifestOrganization> mOrganizations;
    private List<ManifestResource> mResources;

    public ScoManifest(ManifestMetadata metaData,
                       List<ManifestOrganization> manifestOrganizations,
                       List<ManifestResource> manifestResources) {
        mMetaData = metaData;
        mOrganizations = manifestOrganizations;
        mResources = manifestResources;
    }

    public ManifestMetadata getMetaData() { return mMetaData; }
    public List<ManifestOrganization> getOrganizations() { return mOrganizations; }
    public List<ManifestResource> getResources() { return mResources; }
}
