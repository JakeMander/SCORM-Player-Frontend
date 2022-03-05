package com.example.myapplication.ui.scoItems;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.data.AuthDataRepository;
import com.example.myapplication.data.ScoRepository;
import com.example.myapplication.ui.scoItems.fragments.ScoItemsViewModel;

public class ScoItemsActivityVMFactory implements ViewModelProvider.Factory {

    private Context applicationContext;

    public ScoItemsActivityVMFactory(Context context) {
        this.applicationContext = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        AuthDataRepository authDataRepo = AuthDataRepository.getInstance(applicationContext);
        if (modelClass.isAssignableFrom(ScoItemsActivityViewModel.class)) {
            return (T) new ScoItemsActivityViewModel(authDataRepo);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
