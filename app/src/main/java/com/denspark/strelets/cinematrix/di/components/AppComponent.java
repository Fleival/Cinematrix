package com.denspark.strelets.cinematrix.di.components;


import android.app.Application;

import com.denspark.strelets.cinematrix.App;
import com.denspark.strelets.cinematrix.di.module.ActivityModule;
import com.denspark.strelets.cinematrix.di.module.AppModule;
import com.denspark.strelets.cinematrix.di.module.ExploreFragmentModule;
import com.denspark.strelets.cinematrix.di.module.MovieActivityModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;


@Singleton
@Component(modules={
        AndroidInjectionModule.class,
        ActivityModule.class,
        ExploreFragmentModule.class,
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
