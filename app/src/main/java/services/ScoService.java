package services;

import com.example.myapplication.data.model.Credentials;
import com.example.myapplication.data.model.DownloadedSco;
import com.example.myapplication.data.model.LoggedInUser;
import com.example.myapplication.data.model.ScoRecord;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ScoService {

    @GET("/SCORMRepo/listscos")
    Call<List<ScoRecord>> listScos(@Header("Authorization") String authorization);

    @GET("/SCORMRepo/downloadsco/{scoId}")
    Call<DownloadedSco> downloadSco(@Header("Authorization") String authorization,
                                    @Path("scoId") String scoId);
}
