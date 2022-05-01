package com.example.myapplication.ui.scoSplashPage.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.data.model.ManifestOrganization;
import com.example.myapplication.data.model.ScoManifest;
import com.example.myapplication.ui.scoItems.fragments.ScoItemAdapter;
import com.example.myapplication.ui.scoSplashPage.ScoManifestResult;
import com.example.myapplication.ui.scoSplashPage.ScoSplashPageViewModel;

/**
 * A fragment to display a paginated list of all available organizations using a
 * Recycler View to load in the launchable SCOs from the manifest. Manifest
 * does this by referencing the parent Sco Splash Activity's View Model.
 */
public class ScoSplashManifestFragment extends Fragment {

    private Context mContext;
    private ScoSplashPageViewModel mSplashViewModel;
    private RecyclerView mScoItemsRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ScoSplashAdapter mScoItemsAdapter;

    public ScoSplashManifestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mContext = getActivity();
            mSplashViewModel = new ViewModelProvider(requireActivity())
                    .get(ScoSplashPageViewModel.class);
        }

        catch (Exception e) {
            Log.wtf(null, "Could Not Create Splash Fragment: " + e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //  Inflate the layout for this fragment: We'll reference this to access the recycler view
        //  in code.
        View rootView = inflater.inflate(R.layout.fragment_sco_splash_manifest,
                container, false);
        mScoItemsRecyclerView = rootView.findViewById(R.id.splashFragRecyclerView);

        // Get currently selected organization to view from the manifest in a paginated form
        // and retrieve the available SCOs from the course.
        mSplashViewModel.getManifestResult().observe(getViewLifecycleOwner(), new Observer<ScoManifestResult>() {
            @Override
            public void onChanged(ScoManifestResult scoManifestResult) {
                ScoManifest manifest = scoManifestResult.getSuccess();

                if (manifest == null) {
                    Log.wtf(null, "Splash Fragment Could Not Load Manifest");
                    return;
                }
                setRecyclerViewayoutManager();
                mScoItemsAdapter = new ScoSplashAdapter(manifest);
                mScoItemsRecyclerView.setAdapter(mScoItemsAdapter);

                //  Once we know we have successfully retrieved the manifest, begin
                //  listening for changes to our indexed organization.
                mSplashViewModel.getIndexedOrganization().observe(getViewLifecycleOwner(),
                        new Observer<ManifestOrganization>() {
                            @Override
                            public void onChanged(ManifestOrganization manifestOrganization) {
                                Activity parentActivity = getActivity();

                                //  Run the adapter update method on the UI thread
                                //  so the recycler view gets updated.
                                if (parentActivity != null && manifestOrganization != null) {
                                    //ToDo: Call Adapter Update Method When Adapter Implemented
                                    ManifestOrganization indexedOrg = mSplashViewModel.getIndexedOrganization()
                                            .getValue();
                                    mScoItemsAdapter.updateScoItems(indexedOrg);
                                }

                                else {
                                    Toast.makeText(getContext(), "Could Not Load Organizations",
                                            Toast.LENGTH_LONG);
                                    Log.i(null, "Organizations Could Not Load: Null Value Provided");
                                }
                            }
                        });

                //  Once the listener is set up, retrieve the first indexable organizations
                //  list of SCOs.
                mSplashViewModel.setIndexedOrganization(0);
            }
        });
        return rootView;
    }

    private void setRecyclerViewayoutManager() {
        int scrollPosition = 0;
        mLayoutManager = new LinearLayoutManager(getActivity());
        mScoItemsRecyclerView.setLayoutManager(mLayoutManager);
        mScoItemsRecyclerView.scrollToPosition(scrollPosition);
    }
}