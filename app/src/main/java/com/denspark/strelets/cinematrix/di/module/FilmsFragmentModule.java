package com.denspark.strelets.cinematrix.di.module;


import com.denspark.strelets.cinematrix.view.fragments.FilmsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FilmsFragmentModule {
    @ContributesAndroidInjector
    abstract FilmsFragment contributeExploreFragment();
}

