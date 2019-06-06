package com.denspark.strelets.cinematrix.di.module;

import androidx.lifecycle.ViewModel;

import com.denspark.strelets.cinematrix.di.key.ViewModelKey;
import com.denspark.strelets.cinematrix.view_models.MovieViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MovieViewModel.class)
    abstract ViewModel bindMovieViewModel(MovieViewModel movieViewModel);
}
