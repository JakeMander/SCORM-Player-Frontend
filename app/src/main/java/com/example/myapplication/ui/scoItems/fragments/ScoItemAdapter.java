package com.example.myapplication.ui.scoItems.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.AuthDataRepository;
import com.example.myapplication.data.ScoRepository;
import com.example.myapplication.data.ScoZipRepository;
import com.example.myapplication.data.model.ScoRecord;
import com.example.myapplication.databinding.ScoItemCardBinding;
import com.example.myapplication.ui.scoSplashPage.ScoSplashActivity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScoItemAdapter extends RecyclerView.Adapter<ScoItemAdapter.ViewHolder> {

    //  List to track the available set of SCO records in the database
    //  that can be represented in the GUI as part of the recycler
    //  list.
    private List<ScoRecord> mTrackedScoRecords = new ArrayList<ScoRecord>();

    private Context mContext;

    //  Create the executor service which will handle the asynchronous writing
    //  of SCO data to the app's storage. For more details see:
    //  https://developer.android.com/guide/background/threadings
    private ExecutorService mFileExecutorService;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ScoItemCardBinding scoItemCardBinding;

        public ViewHolder(ScoItemCardBinding scoItemCardBinding, ScoRepository scoRepo,
                          ScoZipRepository zipRepo)
        {
            super(scoItemCardBinding.getRoot());
            this.scoItemCardBinding = scoItemCardBinding;

            //  Handles the download operation for each SCO. Downloads the bytes,
            //  assembles them into a ZIP and unpacks them into the app's local
            //  Android storage.
            this.scoItemCardBinding.buttonDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Call<ResponseBody> downloadCall = scoRepo.downloadSco(scoItemCardBinding
                            .getScoRecord().getScoID());
                    downloadCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call,
                                               Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(view.getContext(),
                                        scoItemCardBinding.textViewTitle.getText() +
                                                " Was Downloaded", Toast.LENGTH_SHORT).show();
                                try {
                                    zipRepo.saveZip(
                                            view.getContext(),
                                            response.body().bytes(),
                                            scoItemCardBinding.getScoRecord().getScoID(),
                                            scoItemCardBinding);

                                    Toast.makeText(view.getContext(),
                                            scoItemCardBinding.textViewTitle.getText() +
                                                    " Was Installed", Toast.LENGTH_SHORT).show();

                                    scoItemCardBinding.getScoRecord().setIsScoDownloaded(
                                            view.getContext());
                                } catch (IOException e) {
                                    Toast.makeText(view.getContext(), "Could Not Save ZIP: "
                                            + e, Toast.LENGTH_SHORT).show();
                                }
                            }

                            else
                            {
                                String errorMessage = "Download Error: A HTTP Error Occurred";
                                if (response.code() >= 400 && response.code() < 500) {
                                    errorMessage = "Download Failed: Unauthorised Request";
                                }

                                if (response.code() >= 500 && response.code() < 600) {
                                    errorMessage = "Download Failed: Server Side Error";
                                }

                                Toast.makeText(view.getContext(), errorMessage, Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            String failureMessage = "Download Failed: " + t.getMessage();
                            Toast.makeText(view.getContext(), failureMessage, Toast.LENGTH_SHORT)
                            .show();
                        }
                    });
                }
            });

            //  Set the listener to handle user clicks on the delete button. This will
            //  enable the user to free up space by removing SCO's on the fly.
            this.scoItemCardBinding.buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        zipRepo.deleteZip(itemView.getContext(),
                                scoItemCardBinding.getScoRecord().getScoID(),
                                scoItemCardBinding);
                    }
                    catch (IOException e){
                        String errorMessage = itemView.getContext().getString(
                                R.string.sco_unzip_failed_delete);
                        Toast.makeText(itemView.getContext(), errorMessage + e.getMessage(),
                                Toast.LENGTH_SHORT);
                    }
                }
            });

            //  Set the listener to handle user clicks on the run button. This will enable
            //  the user to launch SCO's and start/continue a SCO session.
            this.scoItemCardBinding.buttonRun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //  Package the scoId and send it off to the Sco splash
                    //  page repository so the correct Manifest can be loaded
                    //  and parsed.
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(view.getContext(), ScoSplashActivity.class);
                    String scoId = scoItemCardBinding.getScoRecord().getScoID().toString();
                    String scoTitle = scoItemCardBinding.getScoRecord().getVanityName();
                    String scoAuthor = scoItemCardBinding.getScoRecord().getScoAuthor();
                    bundle.putString("SCOID", scoId);
                    bundle.putString("SCOTITLE", scoTitle);
                    bundle.putString("SCOAUTHOR", scoAuthor);
                    intent.putExtras(bundle);

                    //  Take the user to the SCO splash page ready to interact
                    //  with the SCO Collection.
                    view.getContext().startActivity(intent);
                }
            });


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
        this.mContext = context;
        this.mFileExecutorService = Executors.newFixedThreadPool(4);
    }

    /**
     * Update the tracked list of SCO records to update the recyclerView
     * with the new set of available SCO's.
     */
    public void updateScos(List<ScoRecord> scoRecords) {
        if(mTrackedScoRecords == null) {
            return;
        }

        mTrackedScoRecords.clear();
        mTrackedScoRecords.addAll(scoRecords);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        AuthDataRepository authRepo = AuthDataRepository.getInstance(mContext);
        ScoRepository scoRepo = ScoRepository.getInstance(authRepo);
        ScoZipRepository zipRepo = ScoZipRepository.getInstance(mFileExecutorService);


        //  Inflate the Sco Card UI using the retrieved binding.
        ScoItemCardBinding item = ScoItemCardBinding.inflate(
                layoutInflater, parent, false);

        /*
            ToDo: Get reference to scoRepo, inject it into ViewHolder, run download
            ToDo: Save file bytes/unzip using SCODiskRepo
             */
        return new ViewHolder(item, scoRepo, zipRepo);
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
        ScoRecord boundScoRecord = mTrackedScoRecords.get(position);
        holder.bind(boundScoRecord);

        //  Check the disk to see if the file exists.
        boundScoRecord.setIsScoDownloaded(mContext);
    }

    @Override
    public int getItemCount() {
        if (mTrackedScoRecords != null) {
            return mTrackedScoRecords.size();
        }
        return 0;
    }
}
