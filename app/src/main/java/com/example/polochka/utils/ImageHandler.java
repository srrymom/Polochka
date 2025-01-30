package com.example.polochka.utils;

import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class ImageHandler {
    private final Fragment fragment;
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;

    public ImageHandler(Fragment fragment, ImageView previewImage, Consumer<Uri> onImagePicked) {
        this.fragment = fragment;

        pickMediaLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        Glide.with(fragment).load(uri).into(previewImage);
                    } else {
                        Log.d("ImageHandler", "Не выбрано изображения");
                    }
                    onImagePicked.accept(uri);  // вызываем переданный метод
                }
        );
    }

    public void pickImage() {
        pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }
}
