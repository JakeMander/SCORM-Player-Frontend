package com.example.myapplication.ui.scoItems.fragments;

import static android.content.ContentValues.TAG;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;

public class ScoItemsFragment extends Fragment {

    private ScoItemsViewModel mViewModel;
    private RecyclerView mScoListRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ScoItemAdapter mScoItemAdapter;

    public static ScoItemsFragment newInstance() {
        return new ScoItemsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Context appContext = getActivity().getApplicationContext();
            Toast.makeText(appContext, "Created ScoItems Frag", Toast.LENGTH_LONG);
            mViewModel = new ViewModelProvider(this,
                    new ScoItemsViewModelFactory(appContext)).get(ScoItemsViewModel.class);
        }
        catch (Exception e) {
            Log.wtf(null, e);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        try {
            //  Inflate the view holding our list of SCOs so we can reference the view and extract
            //  the recyclerView responsible for displaying our list of SCOs.
            View rootView = inflater.inflate(R.layout.sco_items_fragment, container, false);
            mScoListRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_sco_items);

            mViewModel.getScos().observe(getViewLifecycleOwner(), new Observer<ScoItemsResult>() {
                @Override
                public void onChanged(ScoItemsResult scoItemsResult) {
                    Activity parentActivity = getActivity();

                    //  If we can successfully get some SCO Results, update the UI
                    //  by running our adapter update method on the UI thread (the UI
                    //  thread needs to be used, otherwise the call to "notfiyDataSetChanged"
                    //  will not run successfully.
                    if (parentActivity != null && scoItemsResult.getSuccess() != null) {
                        getActivity().runOnUiThread(
                                () -> mScoItemAdapter.updateScos(scoItemsResult.getSuccess()));
                    }

                    //  No SCO update could occur. Update the UI with an error toast.
                    else {
                        Context context = getActivity().getBaseContext();
                        Toast.makeText(context, "Could Not Load SCO's", Toast.LENGTH_SHORT);
                        Log.i(null, "SCO Update Failed");
                    }
                }
            });

            setRecyclerViewLayoutManager();

            mScoItemAdapter = new ScoItemAdapter(getContext());
            mScoListRecyclerView.setAdapter(mScoItemAdapter);

            //  Attempt to update our UI by firing a call off to the API "listScos" endpoint.
            mViewModel.listScos();
            return rootView;
        }

        catch (Exception e) {
            Log.wtf(null, e);
            return null;
        }
    }

    //  Generate a new Linear LayoutManager which we'll use to format recyclerView list
    //  (i.e. display the recyclerView as a list). Assign this new manager to the RecycerView.
    private void setRecyclerViewLayoutManager() {
        int scrollPosition = 0;

        mLayoutManager = new LinearLayoutManager(getActivity());
        mScoListRecyclerView.setLayoutManager(mLayoutManager);
        mScoListRecyclerView.scrollToPosition(scrollPosition);
    }

}