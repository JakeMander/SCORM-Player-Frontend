package com.example.myapplication.ui.scoItems.fragments;

import androidx.annotation.Nullable;

import com.example.myapplication.data.model.DownloadedSco;
import com.example.myapplication.data.model.ScoRecord;

import java.util.List;

public class ScoDownloadResult {
    @Nullable
    private DownloadedSco success;
    @Nullable
    private Integer error;

    ScoDownloadResult(@Nullable Integer error) {
        this.error = error;
    }

    ScoDownloadResult(@Nullable DownloadedSco success) {
        this.success = success;
    }

    @Nullable
    DownloadedSco getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
