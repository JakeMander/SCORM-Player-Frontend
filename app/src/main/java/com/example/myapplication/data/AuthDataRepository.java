package com.example.myapplication.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.myapplication.R;

import com.example.myapplication.data.model.LoggedInUser;

public class AuthDataRepository {

    private static volatile AuthDataRepository instance;

    private static Context appContext;
    private SharedPreferences sharedPreferences;

    private AuthDataRepository(Context context)
    {
        appContext = context;
        sharedPreferences = context.getSharedPreferences(
                context.getString(
                        R.string.auth_shared_preference_file_key
                ),
                Context.MODE_PRIVATE);
    }

    public static AuthDataRepository getInstance(Context context) {
        if (instance == null) {
            instance = new AuthDataRepository(context);
        }
        return instance;
    }

    /**
     * Store the current authenticated user's username
     * and auth token details to disk via a private
     * secure token. THIS STORAGE IS NOT SECURE! For
     * production, be sure to add some kind of encryption
     * to prevent token theft. Each component is stored
     * as it's own shared preference.
     * @param userDetails The user details (i.e. the username
     *                    and auth token) to store to disk
     *                    as individual shared preferences.
     */
    public void storeAuthData(LoggedInUser userDetails)
    {
        String retrievedToken = userDetails.getAuthenticationToken();
        String retrievedUsername = userDetails.getUsername();

        if (retrievedToken.isEmpty() || retrievedToken == null) {
            Log.wtf(null, "Empty Token Was Saved To Shared Preference");
        }

        if (retrievedUsername.isEmpty() || retrievedUsername == null) {
            Log.wtf(null, "Empty Username Was Saved To Shared Preference");
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(
                appContext.getString(
                        R.string.auth_token_key
                ),
                retrievedToken
        );

        editor.putString(
            appContext.getString(
                    R.string.auth_username_key
            ),
                retrievedUsername
        );

        editor.apply();
    }

    /**
     * Retrieves the current authenticated users username
     * and auth token encapsulated as a "LoggedInUser" object.
     * @return The username and auth token currently attributed
     * to the user that was logged in last. Returns null if no
     * data is available.
     */
    public LoggedInUser retrieveAuthData()
    {
        try
        {
            String username = getUsernameSharedPreference();
            String authToken = getAuthTokenSharedPreference();

            return new LoggedInUser(username, authToken);

        }
        catch (IllegalStateException e)
        {
            Log.wtf("AuthDataRepo",
                    String.format(
                            "Could Not Retrieve Auth Data:" +
                    "%s", e.getMessage())
            );
            return null;
        }
    }

    /**
     * Returns the username of the current authenticated
     * user from the associated shared preference.
     * @return A string representing the username of the
     *         currently authenticated user.
     * @throws IllegalStateException
     */
    private String getUsernameSharedPreference() {
        String usernameKey = appContext.getResources()
                .getString(R.string.auth_username_key);
        String username = sharedPreferences.getString(usernameKey,
                "Empty");
        if (username == null || username.isEmpty()) {
            throw new IllegalStateException(
                    "Retrieved Username Shared Preference" +
                            " Was Null");
        }
        return username;
    }

    /**
     * Returns the authToken of the current authenticated
     * user from the associated shared preference.
     * @return A string representing the auth token of the
     *         currently authenticated user.
     * @throws IllegalStateException
     */
    private String getAuthTokenSharedPreference() {
        String authTokenKey = appContext.getResources()
                .getString(R.string.auth_token_key);
        String authToken = sharedPreferences.getString(authTokenKey,
                "Empty");

        if (authToken == null || authToken.isEmpty()) {
            throw new IllegalStateException(
                    "Retrieved Auth Token Shared Preference" +
                            " Was Null");
        }
        return "Bearer " + authToken;
    }
}
