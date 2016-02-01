package com.github.openwebnet.database;

import android.content.Context;

import com.github.openwebnet.component.Injector;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class DatabaseRealm {

    private static final String DATABASE_NAME = "openwebnet.realm";
    private static final int DATABASE_VERSION = 1;

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
        return new RealmConfiguration.Builder(mContext)
            .name(DATABASE_NAME)
            .schemaVersion(DATABASE_VERSION)
            .build();
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

    public <T extends RealmObject> List<T> findCopyWhere(Class<T> clazz, String field, Integer value) {
        RealmResults<T> results = query(clazz).equalTo(field, value).findAll();
        return getRealmInstance().copyFromRealm(results);
    }

    public <T extends RealmObject> List<T> findCopyWhere(Class<T> clazz, String field, Boolean value) {
        RealmResults<T> results = query(clazz).equalTo(field, value).findAll();
        return getRealmInstance().copyFromRealm(results);
    }

}
