package com.denspark.strelets.cinematrix.glide;

import androidx.annotation.NonNull;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.request.BaseRequestOptions;

@GlideExtension
public class MyAppExtension {
    // Size of mini thumb in pixels.
    private static final int MINI_THUMB_SIZE = 100;

    private MyAppExtension() { } // utility class

    @NonNull
    @GlideOption
    public static BaseRequestOptions<?> miniThumb(BaseRequestOptions<?> options) {
        return options
                .fitCenter()
                .override(MINI_THUMB_SIZE);
    }
}
