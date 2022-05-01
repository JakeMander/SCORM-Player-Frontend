package com.example.myapplication.ui.scoSplashPage.fragments;

import android.Manifest;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.model.ManifestItem;
import com.example.myapplication.data.model.ManifestOrganization;
import com.example.myapplication.data.model.ScoManifest;
import com.example.myapplication.databinding.ScoSplashOrganizationItemBinding;

import java.util.ArrayList;
import java.util.List;

/*
 TODO:
  Essentially What we're doing is varying the sco's we see by varying
  the paginated organization seen by the adapter. Each time the user presses a
  button on the UI, we'll call the update adapter method. This will signal
  the adapter to rebind the

 */

public class ScoSplashAdapter extends RecyclerView.Adapter<ScoSplashAdapter.ViewHolder> {

    private ScoManifest mManifest;
    private List<ManifestItem> mOrganizationItems;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ScoSplashOrganizationItemBinding scoSplashBinding;
        ManifestItem item;

        public ViewHolder(@NonNull ScoSplashOrganizationItemBinding itemView,
                          ScoManifest manifest) {
            super(itemView.getRoot());
            scoSplashBinding = itemView;
        }

        public void bind(final ManifestItem item) { scoSplashBinding.setScoOrganizationItem(item);}
    }

    public ScoSplashAdapter(ScoManifest manifest) {
        this.mManifest = manifest;
        this.mOrganizationItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        ScoSplashOrganizationItemBinding sco = ScoSplashOrganizationItemBinding.inflate(
                layoutInflater, parent, false);

        return new ViewHolder(sco, mManifest);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManifestItem boundSco = mOrganizationItems.get(position);
        holder.bind(boundSco);
    }

    @Override
    public int getItemCount() {
        if (mOrganizationItems != null) {
            return mOrganizationItems.size();
        }
        return 0;
    }

    public void updateScoItems(ManifestOrganization org) {
        List<ManifestItem> items = org.getNestedItems();

        if (items == null) {
            Log.wtf(null, "Null Items List Passed From Organization: " + org.getTitle());
            return;
        }

        if (items.isEmpty()) {
            Log.w(null, "No Items Found in Organization: " + org.getTitle());
            return;
        }

        //  Recursively iterate over each item, check for nested children items and add
        //  all nested children that are launchable
        List<ManifestItem> allLaunchableItems = retrieveLaunchableItems(items);
        mOrganizationItems.clear();
        mOrganizationItems.addAll(allLaunchableItems);
    }

    public List<ManifestItem> retrieveLaunchableItems(List<ManifestItem> items) {
        List<ManifestItem> launchableItems = new ArrayList<>();
        //  Recursively Iterate over items, adding all child items to our RecyclerView
        //  provided they are linked to a resource.
        for (ManifestItem item: items) {
            if (item.getIdentifierRef() == null) {
                if (item.getChildItems() == null) {
                    Log.wtf(null, "Non-Launchable Item " + item.getTitle() +
                            "Has No Children");
                    continue;
                }
                List<ManifestItem> launchableChildren = retrieveLaunchableItems(item.getChildItems());
                launchableItems.addAll(launchableChildren);
            }

            else if (item.getIdentifierRef() == null) {
                if (item.getChildItems() == null) {
                    Log.wtf(null, "Non-Launchable Item " + item.getTitle() +
                            "Has No Children");
                    continue;
                }
                List<ManifestItem> launchableChildren = retrieveLaunchableItems(item.getChildItems());
                launchableItems.addAll(launchableChildren);
            }

            else {
                launchableItems.add(item);
            }
        }
        return launchableItems;
    }
}
