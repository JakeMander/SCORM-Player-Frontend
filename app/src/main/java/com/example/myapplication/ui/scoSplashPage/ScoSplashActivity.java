package com.example.myapplication.ui.scoSplashPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.data.model.ScoManifest;

import org.w3c.dom.Text;

public class ScoSplashActivity extends AppCompatActivity {

    private static final String scoIdBundleKey = "SCOID";
    private static final String scoTitleBundleKey = "SCOTITLE";
    private static final String scoAuthorBundleKey = "SCOAUTHOR";

    private Context mAppContext;
    private ScoSplashPageViewModel mViewModel;
    private String mScoId;
    private String mScoTitle;
    private String mScoAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppContext = getApplicationContext();
        mViewModel = new ViewModelProvider(this, new ScoSplashPageVMFactory(mAppContext))
                .get(ScoSplashPageViewModel.class);

        // Retrieve the ScoID passed in via the items activity.
        Bundle bundle = getIntent().getExtras();

        mScoId = bundle.getString(scoIdBundleKey, null);
        mScoTitle = bundle.getString(scoTitleBundleKey, "Untitled");
        mScoAuthor = bundle.getString(scoAuthorBundleKey, "Unknown");

        //  If we can't retrieve the scoID from the items activity,
        //  something has gone wrong and the current activity will not
        //  be able to proceed.
        if (mScoId == null) {
            String errorMessage = "Sco Splash Could Not Retrieve Sco ID";
            Log.wtf(null, errorMessage);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            finish();
        }

        else {
            setContentView(R.layout.activity_sco_splash);

            //  Would probably all do better by going into fragments...
            final TextView activityHeader = findViewById(R.id.splashHeading);
            final TextView activitySubheader = findViewById(R.id.splashSubHeading);
            final TextView activitySchema = findViewById(R.id.schema);
            final TextView activitySchemaVersion = findViewById(R.id.schemaVersion);
            final TextView activityOrganizations = findViewById(R.id.organizationCount);
            final TextView activityResources = findViewById(R.id.resourceCount);
            final ProgressBar spinner = findViewById(R.id.scoSplashSpinner);
            final ImageView banner = findViewById(R.id.scoSplashBanner);
            final ConstraintLayout infoLayout = findViewById(R.id.infoContainer);
            final FragmentContainerView scosFragment = findViewById(R.id.scoLoaderFragment);


            mViewModel.getManifestResult().observe(this, new Observer<ScoManifestResult>() {
                @Override
                public void onChanged(ScoManifestResult scoManifestResult) {

                    //  If the manifestResult is changed to null, something
                    //  has gone wrong so close the current activity.
                    if (scoManifestResult == null) {
                        String errorMessage = "Manifest Result Was Changed Unexpectedly To 'null'";
                        Log.wtf(null, errorMessage);
                        Toast.makeText(mAppContext, errorMessage, Toast.LENGTH_LONG).show();
                        finish();
                    }

                    else if (scoManifestResult.getError() != null) {
                        String errorMessage = "Manifest For Sco " + mScoId + "Could Not Be Parsed: " +
                                scoManifestResult.getError();

                        Log.wtf(null, errorMessage);
                        Toast.makeText(mAppContext, errorMessage, Toast.LENGTH_LONG);
                        finish();
                    }

                    else {

                        //  Absolutely horrific way of setting everything up...
                        //  It'll do for "Research" though :-)
                        ScoManifest result = scoManifestResult.getSuccess();
                        String schema = result.getMetaData().getSchema();
                        String schemaVersion = result.getMetaData().getSchemaVersion();
                        int organizationCount = result.getOrganizations().size();
                        int resourceCount = result.getResources().size();
                        spinner.setVisibility(View.GONE);
                        banner.setVisibility(View.VISIBLE);
                        activityHeader.setText(mScoTitle);
                        activitySubheader.setText(getString(R.string.sco_splash_subheader,
                                mScoAuthor));

                        activitySchema.setText(getString(R.string.sco_splash_schema, schema));

                        activitySchemaVersion.setText(getString(R.string.sco_splash_schemaVersion,
                                schemaVersion));

                        activityOrganizations.setText(
                                getString(R.string.sco_splash_organizationCount,
                                        organizationCount));

                        activityResources.setText(
                                getString(R.string.sco_splash_resourceCount,
                                        resourceCount));

                        infoLayout.setVisibility(View.VISIBLE);
                        scosFragment.setVisibility(View.VISIBLE);
                    }
                }
            });
            // Parse the manifest.
            mViewModel.retrieveScoManifest(mScoId);
        }
    }
}