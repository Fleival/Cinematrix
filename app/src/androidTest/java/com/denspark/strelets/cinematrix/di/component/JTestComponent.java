package com.denspark.strelets.cinematrix.di.component;

import com.denspark.strelets.cinematrix.RepositoryTest;
import com.denspark.strelets.cinematrix.di.module.ContextModule;
import com.denspark.strelets.cinematrix.di.module.JTestingModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        JTestingModule.class,
        ContextModule.class
})
public interface JTestComponent {
    void inject(RepositoryTest repositoryTest);
}
