package com.example.myapplication.data.model;

/**
 *  POJO class to represent a resource contained within
 *  the SCORM manifest.
 *
 *  Used by the LMS to identify the SCO's contained within
 *  the package as well as the files and assets used by
 *  each.
 *
 *  Contains the filepath which is used to launch the associated
 *  session by following the "href" attribute.
 */
public class ManifestResource {

    public enum ScormType {
        SCO,
        ASSET
    }

    private String mResourceIdentifier;
    private String mHref;
    private ScormType mScormType;

    public ManifestResource(String resourceIdentifier, String href,
                            ScormType scormType) {
        this.mResourceIdentifier = resourceIdentifier;
        this.mHref = href;
        this.mScormType = scormType;
    }

    public String getResourceIdentifier() { return mResourceIdentifier; }
    public String getHref() { return mHref; }
    public ScormType getScormType() { return mScormType; }
}
