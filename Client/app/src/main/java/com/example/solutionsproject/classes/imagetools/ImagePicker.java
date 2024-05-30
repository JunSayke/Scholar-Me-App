package com.example.solutionsproject.classes.imagetools;

import android.net.Uri;
import androidx.annotation.NonNull;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class ImagePicker implements DefaultLifecycleObserver {

    public interface OnImageSelectedListener {
        void onImageSelected(Uri imageData);
    }

    private final ActivityResultRegistry registry;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private Uri selectedImageData;
    private OnImageSelectedListener listener;

    public ImagePicker(@NonNull ActivityResultRegistry registry) {
        this.registry = registry;
    }

    public void onCreate(@NonNull LifecycleOwner owner) {
        imagePickerLauncher = registry.register("key", owner, new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        selectedImageData = uri;

                        if(this.listener != null) {
                            this.listener.onImageSelected(selectedImageData);
                        }

                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
    }

    public void selectImage(OnImageSelectedListener listener) {
        this.listener = listener;
        imagePickerLauncher.launch("image/*");
    }
}
