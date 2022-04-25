package com.example.myapplication.data;

import com.example.myapplication.data.model.Credentials;
import com.example.myapplication.data.model.LoggedInUser;

import retrofit2.Call;
import services.LoginService;
import services.ServiceGenerator;

/**
 * Class that requests authentication and user information from the remote data source.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
        }
        return instance;
    }

    /**
     * Creates a callable asynchronous task to provide access to the login functionality
     * of the SCORM Player API using the provided user credentials.
     * @param username The username to login as.
     * @param password The associated username password to authenticate.
     * @return  Returns a 'Call' object that can be asynchronously fired to run the
     *          login operation.
     */
    public Call<LoggedInUser> login(String username, String password) {

        Credentials credentials = new Credentials(username, password);
        LoginService loginService = ServiceGenerator.createService((LoginService.class));
        return loginService.login(credentials);
    }
}