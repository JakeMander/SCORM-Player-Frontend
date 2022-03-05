package com.example.myapplication.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String displayName;
    private String authToken;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName, String authToken)
    {
        this.displayName = displayName;
        this.authToken = authToken;
    }
    String getDisplayName() {
        return displayName;
    }
    String getAuthToken() { return authToken; }
}