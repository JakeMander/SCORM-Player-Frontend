package com.example.myapplication.ui.scoItems.fragments;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.R;
import com.example.myapplication.data.AuthDataRepository;
import com.example.myapplication.data.ScoRepository;
import com.example.myapplication.data.model.DownloadedSco;
import com.example.myapplication.data.model.ScoRecord;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScoItemsViewModel extends ViewModel {
    private ScoRepository scoRepository;
    private AuthDataRepository authDataRepository;
    private MutableLiveData<ScoItemsResult> scoRecords = new MutableLiveData();
    private MutableLiveData<ScoDownloadResult> downloadedSco = new MutableLiveData();

    public ScoItemsViewModel(ScoRepository scoRepository, AuthDataRepository authDataRepository) {
        this.scoRepository = scoRepository;
        this.authDataRepository = authDataRepository;
    }

    public MutableLiveData<ScoItemsResult>getScos() { return scoRecords; };
    public MutableLiveData<ScoDownloadResult>getDownloadedSco() { return downloadedSco; }

    public void listScos() {
        Call<List<ScoRecord>> getScosCall = scoRepository.listScos();

        getScosCall.enqueue(new Callback<List<ScoRecord>>() {
            @Override
            public void onResponse(Call<List<ScoRecord>> call, Response<List<ScoRecord>> response) {
                ScoItemsResult result;
                if (response.isSuccessful()) {
                    result = new ScoItemsResult(response.body());
                    Log.i(null, String.format("List Scos Successful: " +
                            "%d", response.code()));
                }
                else {
                    result = new ScoItemsResult(R.string.sco_items_update_failed);
                    Log.wtf(null, String.format("List Scos Failed (%d): %s",
                            response.code(), response.errorBody().toString()));
                }
                scoRecords.setValue(result);
            }

            @Override
            public void onFailure(Call<List<ScoRecord>> call, Throwable t) {
                ScoItemsResult result = new ScoItemsResult(R.string.sco_items_update_failed);
                scoRecords.setValue(result);
            }
        });
    }

    public void downloadSco(String scoID)
    {
        Call<DownloadedSco> downloadSco = scoRepository.downloadSco(scoID);

        downloadSco.enqueue(new Callback<DownloadedSco>() {
            @Override
            public void onResponse(Call<DownloadedSco> call, Response<DownloadedSco> response) {
                ScoDownloadResult result;
                if (response.isSuccessful()) {
                     result = new ScoDownloadResult(response.body());
                }
                else {
                    result = new ScoDownloadResult(R.string.sco_download_failed);
                }
                downloadedSco.setValue(result);
            }

            @Override
            public void onFailure(Call<DownloadedSco> call, Throwable t) {
                ScoDownloadResult result = new ScoDownloadResult(R.string.sco_download_failed);
                downloadedSco.setValue(result);
            }
        });
    }
}