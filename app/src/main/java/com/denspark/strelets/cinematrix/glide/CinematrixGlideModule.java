package com.denspark.strelets.cinematrix.glide;


import android.content.Context;
import androidx.annotation.NonNull;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

@GlideModule
public class CinematrixGlideModule extends AppGlideModule {

    @Override public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        int diskCacheSizeBytes = 1024 * 1024 * 20; // 20 MB
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes));
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        );
    }
}

