package com.denspark.strelets.cinematrix.di.module;


import com.denspark.strelets.cinematrix.fragments.ExploreFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ExploreFragmentModule {
    @ContributesAndroidInjector
    abstract ExploreFragment contributeExploreFragment();
}

