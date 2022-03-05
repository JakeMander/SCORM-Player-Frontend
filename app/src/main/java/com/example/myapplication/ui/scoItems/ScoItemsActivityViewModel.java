package com.example.myapplication.ui.scoItems;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.AuthDataRepository;
import com.example.myapplication.data.model.LoggedInUser;

public class ScoItemsActivityViewModel extends ViewModel {

    private MutableLiveData<String> mLoggedInUser = new MutableLiveData<>();
    private AuthDataRepository mAuthDataRepo = null;

    public ScoItemsActivityViewModel(AuthDataRepository repo) {
        this.mAuthDataRepo = repo;
    }

    public LiveData<String> getLoggedInUser() {
        return mLoggedInUser;
    }

    public void retrieveLoggedInUser() {
        String returnedUser = mAuthDataRepo.retrieveAuthData().getUsername();
        mLoggedInUser.setValue(returnedUser);
    }
}
