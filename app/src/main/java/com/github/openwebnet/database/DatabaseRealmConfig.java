package com.github.openwebnet.database;

import android.content.Context;
import android.text.TextUtils;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.service.KeyStoreService;
import com.google.common.io.BaseEncoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class DatabaseRealmConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseRealmConfig.class);

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "openwebnet.realm";
    private static final String DATABASE_NAME_CRYPT = "openwebnet.crypt.realm";
    private static final String DATABASE_KEY = "com.github.openwebnet.database.DatabaseRealmConfig.DATABASE_KEY";

    private static final boolean DEBUG_DATABASE = false;
    private static final String DEBUG_REALM_KEY = "database.key";

    @Inject
    Context mContext;

    @Inject
    KeyStoreService keyStoreService;

    public DatabaseRealmConfig() {
        Injector.getApplicationComponent().inject(this);
    }

    public RealmConfiguration getConfig() {
        loadOrGenerateKey();

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

        if (DEBUG_DATABASE) {
            try {
                keyStoreService.writeKeyToFile(DEBUG_REALM_KEY, DATABASE_KEY);
            } catch (IOException e) {
                log.error("error writing key to file", e);
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
            .encryptionKey(decodeHexKey(keyStoreService.getKey(DATABASE_KEY)))
            .schemaVersion(DATABASE_VERSION)
            .migration(new MigrationStrategy())
            .build();
    }

    private void migrateToEncryptedConfig(RealmConfiguration unencryptedConfig) throws IOException {
        Realm realm = Realm.getInstance(unencryptedConfig);
        realm.writeEncryptedCopyTo(new File(mContext.getFilesDir(), DATABASE_NAME_CRYPT),
            decodeHexKey(keyStoreService.getKey(DATABASE_KEY)));
        realm.close();
        Realm.deleteRealm(unencryptedConfig);
    }

    private byte[] generateKey() {
        byte[] key = new byte[64];
        new SecureRandom().nextBytes(key);
        return key;
    }

    private void loadOrGenerateKey() {
        String key = keyStoreService.getKey(DATABASE_KEY);
        if (TextUtils.isEmpty(key)) {
            keyStoreService.setKey(DATABASE_KEY, encodeHexKey(generateKey()));
            log.debug("new Realm key");
        }
    }

    private byte[] decodeHexKey(String value) {
        return BaseEncoding.base16().lowerCase().decode(value);
    }

    // Realm key is a 128-character string in hexadecimal format
    private String encodeHexKey(byte[] value) {
        return BaseEncoding.base16().lowerCase().encode(value);
    }

}
