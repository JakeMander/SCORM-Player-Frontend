package com.example.myapplication.ui.scoSplashPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

public class ScoSplashActivity extends AppCompatActivity {

    private static final String scoIdBundleKey = "SCOID";

    private Context mAppContext;
    private ScoSplashPageViewModel mViewModel;
    private String mScoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppContext = getApplicationContext();
        mViewModel = new ViewModelProvider(this, new ScoSplashPageVMFactory(mAppContext))
                .get(ScoSplashPageViewModel.class);

        // Retrieve the ScoID passed in via the items activity.
        Bundle bundle = getIntent().getExtras();

        mScoId = bundle.getString(scoIdBundleKey, null);

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

            final TextView activityHeader = findViewById(R.id.splashHeading);
            final ProgressBar spinner = findViewById(R.id.scoSplashSpinner);

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
                        spinner.setVisibility(View.GONE);

                        // Load Fragment To Display Manifest Details...
                    }
                }
            });
            // Parse the manifest.
            mViewModel.retrieveScoManifest(mScoId);
        }
    }
}