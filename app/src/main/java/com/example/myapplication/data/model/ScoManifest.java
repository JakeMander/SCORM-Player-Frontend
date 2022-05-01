package com.example.myapplication.data.model;

import com.example.myapplication.data.ScoManifestRepository;

import java.util.List;
import java.util.stream.Stream;

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

    /***
     *  Retrieves the Launch Href of the resource that matches the provided resource
     *  Reference.
     * @param resourceRef
     * @return  A string representing the launch URL of the associated matched
     *          manifest resource element.
     * @throws UnsupportedOperationException
     */
    public String GetHrefByResourceRef(String resourceRef) throws UnsupportedOperationException {
        for (ManifestResource resource : mResources) {
            if (resource.getResourceIdentifier().equals(resourceRef)) {
                return resource.getHref();
            }
        }
        throw new UnsupportedOperationException("No Resource Matches The Provided Ref");
    }
}
