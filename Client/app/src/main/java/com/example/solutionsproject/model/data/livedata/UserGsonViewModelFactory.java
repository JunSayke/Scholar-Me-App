package com.example.solutionsproject.model.data.livedata;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class UserGsonViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public UserGsonViewModelFactory(Context context) {
        this.context = context.getApplicationContext(); // Use application context to avoid memory leaks
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserGsonViewModel.class)) {
            return (T) new UserGsonViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
