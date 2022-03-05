package services;

import com.example.myapplication.data.model.Credentials;
import com.example.myapplication.data.model.LoggedInUser;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LoginService {
    @POST("/authentication/login")
    Call<LoggedInUser> login(@Body Credentials credential);
}
