package com.denspark.strelets.cinematrix.di.module;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {
    @Provides
    Context provideContext(){
        return  ApplicationProvider.getApplicationContext();
    }
}
