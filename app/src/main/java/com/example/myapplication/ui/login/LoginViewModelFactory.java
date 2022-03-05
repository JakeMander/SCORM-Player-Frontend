package com.example.myapplication.ui.login;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.example.myapplication.data.AuthDataRepository;
import com.example.myapplication.data.LoginRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private Context appContext;

    public LoginViewModelFactory(Context appContext)
    {
        this.appContext = appContext;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(LoginRepository.getInstance(),
                    AuthDataRepository.getInstance(appContext));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}