package com.github.openwebnet.component.module;

import com.github.openwebnet.repository.RepositoryDomoticDevice;
import com.github.openwebnet.repository.RepositoryDomoticEnvironment;
import com.github.openwebnet.repository.impl.RepositoryDomoticEnvironmentImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

@Module
public class RepositoryModule {

    private final Realm realm;

    public RepositoryModule(Realm realm) {
        this.realm = realm;
    }

    @Provides
    @Singleton
    RepositoryDomoticEnvironment provideEnvironment() {
        return new RepositoryDomoticEnvironmentImpl(realm);
    }

    @Provides
    @Singleton
    RepositoryDomoticDevice provideDevice() {
        return null;
    }

}
