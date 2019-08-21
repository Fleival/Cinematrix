package com.denspark.strelets.cinematrix.di.module;

import com.denspark.strelets.cinematrix.view.activities.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector(modules = ExploreFragmentModule.class)
    abstract MainActivity contributeMainActivity();
}
