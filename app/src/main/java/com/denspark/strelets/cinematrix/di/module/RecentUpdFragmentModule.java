package com.denspark.strelets.cinematrix.di.module;


import com.denspark.strelets.cinematrix.view.fragments.RecentUpdFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class RecentUpdFragmentModule {
    @ContributesAndroidInjector
    abstract RecentUpdFragment contributeExploreFragment();
}

