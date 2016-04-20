package com.github.openwebnet.database;

import android.content.Context;

import com.github.openwebnet.component.Injector;
import com.google.common.io.BaseEncoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class DatabaseRealm {

    private static final Logger log = LoggerFactory.getLogger(DatabaseRealm.class);

    private static final String DATABASE_NAME = "openwebnet.realm";
    private static final String DATABASE_NAME_CRYPT = "openwebnet.crypt.realm";
    private static final int DATABASE_VERSION = 2;

    @Inject
    Context mContext;

    RealmConfiguration realmConfiguration;

    public DatabaseRealm() {
        Injector.getApplicationComponent().inject(this);
    }

    public void setup() {
        if (realmConfiguration == null) {
            realmConfiguration = getRealmConfig();
            Realm.setDefaultConfiguration(realmConfiguration);
        } else {
            throw new IllegalStateException("database already configured");
        }
    }

    private RealmConfiguration getRealmConfig() {
        byte[] realmEncryptionKey = getRealmEncryptionKey();
        log.debug("realmEncryptionKey: {}", BaseEncoding.base16().lowerCase().encode(realmEncryptionKey));

        boolean existsUnencryptedRealm = new File(mContext.getFilesDir(), DATABASE_NAME).exists();
        if (existsUnencryptedRealm) {
            RealmConfiguration realmConfig = new RealmConfiguration.Builder(mContext)
                .name(DATABASE_NAME)
                .schemaVersion(DATABASE_VERSION)
                .migration(new MigrationStrategy())
                .build();

            try {
                Realm realm = Realm.getInstance(realmConfig);
                realm.writeEncryptedCopyTo(new File(mContext.getFilesDir(), DATABASE_NAME_CRYPT), realmEncryptionKey);
                realm.close();
                Realm.deleteRealm(realmConfig);
            } catch (IOException e) {
                log.error("unable to encrypt realm", e);
                return realmConfig;
            }
        }

        RealmConfiguration realmConfigCrypt = new RealmConfiguration.Builder(mContext)
            .name(DATABASE_NAME_CRYPT)
            .encryptionKey(realmEncryptionKey)
            .schemaVersion(DATABASE_VERSION)
            .migration(new MigrationStrategy())
            .build();

        return realmConfigCrypt;
    }

    private byte[] getRealmEncryptionKey() {
        byte[] key = new byte[64];
        new SecureRandom().nextBytes(key);
        return key;
    }

    public Realm getRealmInstance() {
        return Realm.getDefaultInstance();
    }

    public <T extends RealmObject> T add(T model) {
        Realm realm = getRealmInstance();
        realm.beginTransaction();
        realm.copyToRealm(model);
        realm.commitTransaction();
        return model;
    }

    public <T extends RealmObject> T update(T model) {
        Realm realm = getRealmInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(model);
        realm.commitTransaction();
        return model;
    }

    public <T extends RealmObject> void delete(Class<T> clazz, String field, String value) {
        Realm realm = getRealmInstance();
        RealmResults<T> models = realm.where(clazz).equalTo(field, value).findAll();
        realm.beginTransaction();
        models.clear();
        realm.commitTransaction();
    }

    public <T extends RealmObject> void delete(Class<T> clazz, String field, Integer value) {
        Realm realm = getRealmInstance();
        RealmResults<T> models = realm.where(clazz).equalTo(field, value).findAll();
        realm.beginTransaction();
        models.clear();
        realm.commitTransaction();
    }

    private <T extends RealmObject> RealmQuery<T> query(Class<T> clazz) {
        return getRealmInstance().where(clazz);
    }

    public <T extends RealmObject> List<T> find(Class<T> clazz) {
        return query(clazz).findAll();
    }

    public <T extends RealmObject> List<T> findSortedAscending(Class<T> clazz, String field) {
        RealmResults<T> results = query(clazz).findAll();
        results.sort(field, Sort.ASCENDING);
        return results;
    }

    public <T extends RealmObject> Number findMax(Class<T> clazz, String field) {
        return query(clazz).max(field);
    }

    public <T extends RealmObject> List<T> findWhere(Class<T> clazz, String field, String value) {
        return query(clazz).equalTo(field, value).findAll();
    }

    public <T extends RealmObject> List<T> findCopyWhere(Class<T> clazz, String field, Integer value, String orderBy) {
        RealmResults<T> results = query(clazz).equalTo(field, value).findAll();
        if (orderBy != null) {
            results.sort(orderBy, Sort.ASCENDING);
        }
        return getRealmInstance().copyFromRealm(results);
    }

    public <T extends RealmObject> List<T> findCopyWhere(Class<T> clazz, String field, Boolean value, String orderBy) {
        RealmResults<T> results = query(clazz).equalTo(field, value).findAll();
        if (orderBy != null) {
            results.sort(orderBy, Sort.ASCENDING);
        }
        return getRealmInstance().copyFromRealm(results);
    }

}
