package com.example.myapplication.data;

import android.content.Context;

import com.example.myapplication.data.model.DownloadedSco;
import com.example.myapplication.data.model.ScoRecord;

import java.util.List;

import retrofit2.Call;
import services.ScoService;
import services.ServiceGenerator;

/**
 * ScoRepository handles access to all data relating
 * to the SCO content served on a server.
 */
public class ScoRepository {
    private static volatile ScoRepository instance;
    private static AuthDataRepository authRepo;

    public static ScoRepository getInstance(AuthDataRepository authRepoIn) {
        if (instance == null) {
            instance = new ScoRepository();
        }
        authRepo = authRepoIn;
        return instance;
    }

    /**
     * Creates a callable asynchronous task to return a list of available
     * scos from a server.
     * @return Returns a 'Call' object that can be asynchronously fired by
     * an "enqueued" to run the call to the listScos endpoint.
     */
    public Call<List<ScoRecord>> listScos() {
        String authToken = getAuthToken();
        ScoService scoService = ServiceGenerator.createService(ScoService.class);
        Call<List<ScoRecord>> listScoCall = scoService.listScos(authToken);
        return listScoCall;
    }

    public Call<DownloadedSco> downloadSco(String objectId) {
        String authToken = getAuthToken();
        ScoService scoService = ServiceGenerator.createService(ScoService.class);
        Call<DownloadedSco> downloadScoCall = scoService.downloadSco(authToken, objectId);
        return downloadScoCall;
    }

    private String getAuthToken() {
        if (authRepo == null) {
            throw new NullPointerException("Could Not Fetch Auth Token: Auth Repo Is Null");
        }

        String authToken = authRepo.retrieveAuthData().getAuthenticationToken();

        if (authToken == null) {
            throw new NullPointerException("Could Not Fetch Auth Token: No Auth Token Received");
        }

        return authToken;
    }
}