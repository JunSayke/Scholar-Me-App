package com.example.solutionsproject.classes.imagetools;

import android.net.Uri;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class AudioPicker implements DefaultLifecycleObserver {

    public interface OnAudioSelectedListener {
        void onImageSelected(Uri imageData);
    }

    private final ActivityResultRegistry registry;
    private ActivityResultLauncher<String> audioPickerLauncher;
    private Uri selectedImageData;
    private OnAudioSelectedListener listener;

    public AudioPicker(@NonNull ActivityResultRegistry registry) {
        this.registry = registry;
    }

    public void onCreate(@NonNull LifecycleOwner owner) {
        audioPickerLauncher = registry.register("key", owner, new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        Log.d("AudioPicker", "Selected URI: " + uri);
                        selectedImageData = uri;

                        if(this.listener != null) {
                            this.listener.onImageSelected(selectedImageData);
                        }

                    } else {
                        Log.d("AudioPicker", "No media selected");
                    }
                });
    }

    public void selectImage(OnAudioSelectedListener listener) {
        this.listener = listener;
        audioPickerLauncher.launch("audio/*");
    }
}
