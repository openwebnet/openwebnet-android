package com.github.openwebnet.database;

import android.content.Context;
import android.os.Build;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.service.KeyStoreService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class DatabaseRealmConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseRealmConfig.class);

    private static final String DATABASE_NAME = "openwebnet.realm";
    private static final String DATABASE_NAME_CRYPT = "openwebnet.crypt.realm";
    private static final int DATABASE_VERSION = 2;

    @Inject
    Context mContext;

    @Inject
    KeyStoreService keyStoreService;

    public DatabaseRealmConfig() {
        Injector.getApplicationComponent().inject(this);
    }

    public RealmConfiguration getConfig() {
        if (!isEncryptionSupported()) {
            log.warn("realm encryption not supported: API >= 18");
            return getUnencryptedConfig();
        }

        boolean existsUnencryptedRealm = new File(mContext.getFilesDir(), DATABASE_NAME).exists();

        if (existsUnencryptedRealm) {
            RealmConfiguration unencryptedConfig = getUnencryptedConfig();
            try {
                migrateToEncryptedConfig(unencryptedConfig);
                log.debug("migration to encrypted realm successful");
            } catch (IOException e) {
                log.error("error migrating encrypted realm", e);
                return unencryptedConfig;
            }
        }

        return getEncryptedConfig();
    }

    private RealmConfiguration getUnencryptedConfig() {
        return new RealmConfiguration.Builder(mContext)
            .name(DATABASE_NAME)
            .schemaVersion(DATABASE_VERSION)
            .migration(new MigrationStrategy())
            .build();
    }

    private RealmConfiguration getEncryptedConfig() {
        return new RealmConfiguration.Builder(mContext)
            .name(DATABASE_NAME_CRYPT)
            .encryptionKey(keyStoreService.getKey())
            .schemaVersion(DATABASE_VERSION)
            .migration(new MigrationStrategy())
            .build();
    }

    private boolean isEncryptionSupported() {
        log.debug("current OS version is {}", Build.VERSION.SDK_INT);
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    private void migrateToEncryptedConfig(RealmConfiguration unencryptedConfig) throws IOException {
        Realm realm = Realm.getInstance(unencryptedConfig);
        realm.writeEncryptedCopyTo(new File(mContext.getFilesDir(), DATABASE_NAME_CRYPT),
            keyStoreService.getKey());
        realm.close();
        Realm.deleteRealm(unencryptedConfig);
    }

}
