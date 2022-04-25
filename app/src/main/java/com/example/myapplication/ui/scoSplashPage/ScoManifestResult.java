package com.example.myapplication.ui.scoSplashPage;

import com.example.myapplication.data.model.ScoManifest;

/**
 *  Used to signal to the UI whether or not the manifest
 *  for the current SCO Collection was a success.
 */
public class ScoManifestResult {
    private ScoManifest mRetrievedManifest;
    private String mErrorMessage;

    //  Instantiate the result to a "Failed" state.
    ScoManifestResult(String errorMessage) { this.mErrorMessage = errorMessage; };

    //  Instantiate the result to a "Success" state.
    ScoManifestResult(ScoManifest retrievedManifest) {
        this.mRetrievedManifest = retrievedManifest;
        this.mErrorMessage = null;
    }

    ScoManifest getSuccess() { return mRetrievedManifest; }
    String getError() { return mErrorMessage; }

}
