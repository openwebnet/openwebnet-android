package com.github.openwebnet.component.module;

import com.github.openwebnet.database.DatabaseRealm;
import com.github.openwebnet.database.DatabaseRealmConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class DatabaseModuleTest {

    @Provides
    @Singleton
    public DatabaseRealm provideDatabaseRealm() {
        return mock(DatabaseRealm.class);
    }

    @Provides
    @Singleton
    public DatabaseRealmConfig provideDatabaseRealmConfig() {
        return mock(DatabaseRealmConfig.class);
    }

}
