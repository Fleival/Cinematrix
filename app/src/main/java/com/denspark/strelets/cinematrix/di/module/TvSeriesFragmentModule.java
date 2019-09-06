package com.denspark.strelets.cinematrix.di.module;


import com.denspark.strelets.cinematrix.view.fragments.TvSeriesFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class TvSeriesFragmentModule {
    @ContributesAndroidInjector
    abstract TvSeriesFragment contributeExploreFragment();
}

