package com.example.myapplication.ui.scoSplashPage;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.ScoManifestRepository;
import com.example.myapplication.data.model.ManifestOrganization;
import com.example.myapplication.data.model.ScoManifest;

import java.io.FileNotFoundException;
import java.util.List;

public class ScoSplashPageViewModel extends ViewModel {
    private MutableLiveData<ScoManifestResult> mManifestResult = new MutableLiveData<>();
    private MutableLiveData<ManifestOrganization> mSelectedOrganization = new MutableLiveData<>();
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
     *  To allow for a cleaner UI, paginate the displayed SCOs by only
     *  displaying the SCO's of an individual organization.
     */
    public MutableLiveData<ManifestOrganization> getIndexedOrganization() {
        return this.mSelectedOrganization; }

    /**
     *  Sets the new indexed organization for the model thus updating any observing
     *  components with a new set of referenced SCO's.
     * @param index An index to reference the manifest's list of SCOs.
     * @throws ArrayIndexOutOfBoundsException
     */
    public void setIndexedOrganization(int index)
            throws ArrayIndexOutOfBoundsException {

        ScoManifestResult result = mManifestResult.getValue();

        if (result == null) {
            Log.w(null, "Result Has Not Loaded");
            return;
        }

        ScoManifest retrievedManifest = mManifestResult.getValue().getSuccess();
        if (retrievedManifest != null) {

            List<ManifestOrganization> organizations = retrievedManifest.getOrganizations();
            int numberOfOrganizations = organizations.size();

            if (index < 0) {
                index = numberOfOrganizations - 1;
            }

            if (index > numberOfOrganizations - 1) {
                index = 0;
            }

            this.mSelectedOrganization.setValue(retrievedManifest.getOrganizations().get(index));
        }
    }

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