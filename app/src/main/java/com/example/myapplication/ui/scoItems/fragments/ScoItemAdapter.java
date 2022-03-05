package com.example.myapplication.ui.scoItems.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.ScoRepository;
import com.example.myapplication.data.model.ScoRecord;
import com.example.myapplication.databinding.ScoItemCardBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScoItemAdapter extends RecyclerView.Adapter<ScoItemAdapter.ViewHolder> {

    private List<ScoRecord> trackedScoRecords = new ArrayList<ScoRecord>();
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ScoItemCardBinding scoItemCardBinding;

        public ViewHolder(ScoItemCardBinding scoItemCardBinding)
        {
            super(scoItemCardBinding.getRoot());
            this.scoItemCardBinding = scoItemCardBinding;
            this.scoItemCardBinding.buttonDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(),
                            scoItemCardBinding.textViewTitle.getText() + " Clicked",
                            Toast.LENGTH_SHORT).show();
                }
            })
            ;
        }

        /**
         *  Binds the "scoRecord" data variable stored in the sco card's
         *  XML file to a provided scoRecord object, thus achieving data
         *  binding.
         * @param record The ScoRecord model object that will be bound to
         *               the "ScoRecord" variable that will bind each
         *               member variable to it's associated UI components
         *               in the sco card's.
         */
        public void bind(final ScoRecord record)
        {
            scoItemCardBinding.setScoRecord(record);
        }
    }

    public ScoItemAdapter(Context context) {
        this.context = context;
    }

    /**
     * Update the tracked list of SCO records to update the recyclerView
     * with the new set of available SCO's.
     */
    public void updateScos(List<ScoRecord> scoRecords) {
        if(trackedScoRecords == null) {
            return;
        }

        trackedScoRecords.clear();
        trackedScoRecords.addAll(scoRecords);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        //  Inflate the Sco Card UI using the retrieved binding.
        ScoItemCardBinding item = ScoItemCardBinding.inflate(
                layoutInflater, parent, false);

        /*
            ToDo: Get reference to scoRepo, inject it into ViewHolder, run download
            ToDo: Save file bytes/unzip using SCODiskRepo
             */
        return new ViewHolder(item);
    }

    /**
     * When the List of Scos is ready to be rendered, each UI view needs to have it's
     * data bound to each retrieved model. In this instance, we bind the "scoRecord" data
     * variable in the XML file to a scoRecord Model object. This allows the XML file to
     * directly extract the properties and methods it needs to populate the UI.
     * @param holder The ViewHolder that holds the UI instance that a model retrieved
     *               from the list of SCOS is to be bound to.
     * @param position The index that references which SCO has been selected from the list.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        ScoRecord boundScoRecord = trackedScoRecords.get(position);
        holder.bind(boundScoRecord);
    }

    @Override
    public int getItemCount() {
        if (trackedScoRecords != null) {
            return trackedScoRecords.size();
        }
        return 0;
    }
}
