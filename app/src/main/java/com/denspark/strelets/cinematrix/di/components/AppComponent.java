package com.denspark.strelets.cinematrix.di.components;


import android.app.Application;

import com.denspark.strelets.cinematrix.App;
import com.denspark.strelets.cinematrix.di.module.ActivityModule;
import com.denspark.strelets.cinematrix.di.module.AppModule;
import com.denspark.strelets.cinematrix.di.module.FilmsFragmentModule;
import com.denspark.strelets.cinematrix.di.module.MovieActivityModule;
import com.denspark.strelets.cinematrix.di.module.TvSeriesFragmentModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;


@Singleton
@Component(modules={
        AndroidInjectionModule.class,
        ActivityModule.class,
        FilmsFragmentModule.class,
        TvSeriesFragmentModule.class,
        AppModule.class,
        MovieActivityModule.class
})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }

    void inject(App app);
}
