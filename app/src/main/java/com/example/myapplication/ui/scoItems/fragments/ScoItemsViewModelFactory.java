package com.example.myapplication.ui.scoItems.fragments;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.data.AuthDataRepository;
import com.example.myapplication.data.LoginRepository;
import com.example.myapplication.data.ScoRepository;
import com.example.myapplication.ui.login.LoginViewModel;

public class ScoItemsViewModelFactory implements ViewModelProvider.Factory {

    private Context applicationContext;

    public ScoItemsViewModelFactory(Context context) {this.applicationContext = context; }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        ScoRepository scoRepo = ScoRepository.getInstance(
                AuthDataRepository.getInstance(applicationContext));

        AuthDataRepository authRepo = AuthDataRepository.getInstance(applicationContext);


        if (modelClass.isAssignableFrom(ScoItemsViewModel.class)) {
            return (T) new ScoItemsViewModel(scoRepo, authRepo);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
