package com.github.openwebnet.component.module;

import com.github.openwebnet.database.DatabaseRealm;
import com.github.openwebnet.database.DatabaseRealmConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    public DatabaseRealm provideDatabaseRealm() {
        return new DatabaseRealm();
    }

    @Provides
    @Singleton
    public DatabaseRealmConfig provideDatabaseRealmConfig() {
        return new DatabaseRealmConfig();
    }
}
