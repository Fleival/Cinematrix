package com.denspark.strelets.cinematrix.di.module;


import com.denspark.strelets.cinematrix.view.fragments.ProfileFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ProfileFragmentModule {
    @ContributesAndroidInjector
    abstract ProfileFragment contributeExploreFragment();
}

