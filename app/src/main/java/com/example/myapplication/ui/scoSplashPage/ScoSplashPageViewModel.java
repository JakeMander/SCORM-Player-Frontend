package com.example.myapplication.ui.scoSplashPage;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.ScoManifestRepository;
import com.example.myapplication.data.model.ScoManifest;

import java.io.FileNotFoundException;

public class ScoSplashPageViewModel extends ViewModel {
    private MutableLiveData<ScoManifestResult> mManifestResult = new MutableLiveData<>();
    private ScoManifestRepository mScoManifestRepo = null;

    public ScoSplashPageViewModel (ScoManifestRepository repo) {
        this.mScoManifestRepo = repo;
    }

    /**
     *  Retrieve a reference to the Manifest Result Live Data so the UI
     *  can observe changes that occur as part of the manifest parse.
     * @return  A result that indicates the success or failure of a manifest
     *          parse.
     */
    public MutableLiveData<ScoManifestResult> getManifestResult() { return this.mManifestResult; }

    /**
     *  Initiates the parsing of the manifest found under the associated
     *  scoID's sco collection directory. Manifest parsing is launched on
     *  a new thread and updates the ScoManifestResult live data via a
     *  callback.
     * @param scoId The SCO ID of the sco collection whose manifest is to
     *              be parsed.
     */
    public void retrieveScoManifest(String scoId) {
        try {
                //  Call the repo function to extract the SCO manifest.
                //  As the manifest is parsed on another thread, we'll need
                //  a callback to communicate the manifest back to the UI
                //  thread once it's been successfully parsed.
                mScoManifestRepo.getScoManifest(scoId,
                    (parsedManifest) -> {
                        ScoManifestResult retrievedManifestResult;

                        //  If the callback ends up being passed "null", there
                        //  has been an error which we need to signal the UI with,
                        //  so create a result instantiated to a "Failed" state.
                        if (parsedManifest == null) {
                            retrievedManifestResult = new ScoManifestResult(
                                    "Could Not Retrieve The Manifest");
                        }

                        else {
                            retrievedManifestResult = new ScoManifestResult(
                                    parsedManifest
                            );
                        }

                        //  As the LiveData is being updated from a worker thread,
                        //  "postValue" must be used here as per the Android Docs:
                        //  https://developer.android.com/topic/libraries/architecture/livedata
                        mManifestResult.postValue(retrievedManifestResult);
                    });
        }

        catch (FileNotFoundException e) {
            String errorMessage = "Could Not Retrieve Sco Manifest: " + e.getMessage();
            Log.wtf(null, errorMessage);
            mManifestResult.setValue(new ScoManifestResult(errorMessage));
        }
    }
}