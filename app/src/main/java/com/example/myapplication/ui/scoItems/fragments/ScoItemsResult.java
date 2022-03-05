package com.example.myapplication.ui.scoItems.fragments;

import androidx.annotation.Nullable;

import com.example.myapplication.data.model.ScoRecord;

import java.util.List;

public class ScoItemsResult {
    @Nullable
    private List<ScoRecord> success;
    @Nullable
    private Integer error;

    ScoItemsResult(@Nullable Integer error) {
        this.error = error;
    }

    ScoItemsResult(@Nullable List<ScoRecord> success) {
        this.success = success;
    }

    @Nullable
    List<ScoRecord> getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
