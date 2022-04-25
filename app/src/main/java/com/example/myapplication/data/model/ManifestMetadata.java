package com.example.myapplication.data.model;

/**
 *  POJO class to represent the associated metadata
 *  contained within the SCO collection.
 *
 *  Used to provide various details to the LMS
 *  including the schema and version of SCORM
 *  that has been used to build the package.
 */
public class ManifestMetadata {
    private String mSchema;
    private String mSchemaVersion;

    public ManifestMetadata(String schema, String schemaVersion) {
        this.mSchema = schema;
        this.mSchemaVersion = schemaVersion;
    }

    public String getSchema() { return mSchema; }
    public String getSchemaVersion() {return mSchemaVersion; }

}
