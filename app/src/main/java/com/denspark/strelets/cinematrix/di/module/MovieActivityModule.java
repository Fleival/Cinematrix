package com.denspark.strelets.cinematrix.di.module;

import com.denspark.strelets.cinematrix.view.activities.MovieActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MovieActivityModule {
    @ContributesAndroidInjector
    abstract MovieActivity contributeMovieActivity();
}
