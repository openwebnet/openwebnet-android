package com.github.openwebnet.component.module;

import com.github.openwebnet.repository.RepositoryDomoticDevice;
import com.github.openwebnet.repository.RepositoryDomoticEnvironment;
import com.github.openwebnet.repository.impl.RepositoryDomoticEnvironmentImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    RepositoryDomoticEnvironment provideEnvironment() {
        return new RepositoryDomoticEnvironmentImpl();
    }

    @Provides
    @Singleton
    RepositoryDomoticDevice provideDevice() {
        return null;
    }

}
