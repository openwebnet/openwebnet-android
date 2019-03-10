package com.github.openwebnet.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.service.PreferenceService;
import com.google.common.io.BaseEncoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * See this issue for more details about KeyStore and SecurePreferences
 * https://github.com/openwebnet/openwebnet-android/issues/46
 */
public class DatabaseRealmConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseRealmConfig.class);

    public static final int DATABASE_VERSION = 11;
    private static final String DATABASE_NAME = "openwebnet.realm";
    private static final String DATABASE_NAME_CRYPT = "openwebnet.crypt.realm";

    private static final boolean DEBUG_DATABASE = false;
    private static final String DEBUG_REALM_KEY = "database.key";
    private static final String PREFERENCE_DATABASE_KEY = "com.github.openwebnet.database.DatabaseRealmConfig.PREFERENCE_DATABASE_KEY";
    private static final String PREFERENCE_DATABASE_KEY_OLD = "com.github.openwebnet.database.DatabaseRealmConfig.PREFERENCE_DATABASE_KEY_OLD";

    @Inject
    Context mContext;

    @Inject
    PreferenceService preferenceService;

    public DatabaseRealmConfig() {
        Injector.getApplicationComponent().inject(this);
    }

    /**
     * @return RealmConfiguration
     */
    public RealmConfiguration getConfig() {
        initRealmKey();
        Realm.init(mContext);

        if (DEBUG_DATABASE) {
            writeKeyToFile();
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
        return new RealmConfiguration.Builder()
            .name(DATABASE_NAME)
            .schemaVersion(DATABASE_VERSION)
            .migration(new MigrationStrategy())
            .build();
    }

    private RealmConfiguration getEncryptedConfig() {
        return new RealmConfiguration.Builder()
            .name(DATABASE_NAME_CRYPT)
            .encryptionKey(getRealmKey())
            .schemaVersion(DATABASE_VERSION)
            .migration(new MigrationStrategy())
            .build();
    }

    private void migrateToEncryptedConfig(RealmConfiguration unencryptedConfig) throws IOException {
        Realm realm = Realm.getInstance(unencryptedConfig);
        realm.writeEncryptedCopyTo(new File(mContext.getFilesDir(), DATABASE_NAME_CRYPT), getRealmKey());
        realm.close();
        Realm.deleteRealm(unencryptedConfig);
    }

    private byte[] generateKey() {
        byte[] key = new byte[64];
        new SecureRandom().nextBytes(key);
        return key;
    }

    // https://github.com/openwebnet/openwebnet-android/issues/82
    // quick fix: REMOVE scottyab/secure-preferences library
    // long term solution: move to a different library
    private void initRealmKey() {
        String DEFAULT_DATABASE_KEY = "";
        SharedPreferences securePreferences = preferenceService.getSecurePreferences();

        // migrate key
        if (securePreferences.contains(PREFERENCE_DATABASE_KEY)) {
            String key = preferenceService.getSecurePreferences().getString(PREFERENCE_DATABASE_KEY, DEFAULT_DATABASE_KEY);

            if (!DEFAULT_DATABASE_KEY.equals(key)) {
                preferenceService.saveInsecureRealmKey(key);
            }

            // preserve old key in case the bug is fixed
            securePreferences.edit().putString(PREFERENCE_DATABASE_KEY_OLD, key).apply();
            // remove key from bugged library
            securePreferences.edit().remove(PREFERENCE_DATABASE_KEY).apply();
            log.info("database key migrated from scottyab/secure-preferences");
        }

        // new installation
        if (!preferenceService.containsInsecureRealmKey()) {
            // realm key is a 128-character string in hexadecimal format
            String key = BaseEncoding.base16().lowerCase().encode(generateKey());
            preferenceService.saveInsecureRealmKey(key);
            log.info("new database key stored in preferences");
        }
    }

    private byte[] getRealmKey() {
        String key = preferenceService.getInsecureRealmKey();
        log.info("getRealmKey: {}", key);
        return BaseEncoding.base16().lowerCase().decode(key);
    }

    private void writeKeyToFile() {
        try {
            File file = new File(mContext.getFilesDir(), DEBUG_REALM_KEY);
            FileOutputStream stream = new FileOutputStream(file);
            String key = preferenceService.getInsecureRealmKey();
            stream.write(key.getBytes());
            stream.close();
        } catch (IOException e) {
            log.error("error writing key to file", e);
        }
    }

}
