package com.example.myapplication.ui.scoSplashPage;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.data.ScoManifestRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScoSplashPageVMFactory implements ViewModelProvider.Factory {
    private Context mAppContext;
    private ExecutorService mExecutorService;

    public ScoSplashPageVMFactory(Context context) {
        mAppContext = context;
        mExecutorService = Executors.newFixedThreadPool(4);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        ScoManifestRepository scoManifestRepo = ScoManifestRepository.getInstance(mAppContext,
                mExecutorService);

        if (aClass.isAssignableFrom(ScoSplashPageViewModel.class)) {
            return (T) new ScoSplashPageViewModel(scoManifestRepo);
        }

        else {
            throw new IllegalArgumentException("Unknown ViewModel Class");
        }
    }
}
