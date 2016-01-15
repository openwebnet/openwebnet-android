package com.github.openwebnet.repository.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.repository.CommonRealmRepository;
import com.github.openwebnet.repository.DatabaseHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;

import static com.google.common.base.Preconditions.checkState;

public abstract class CommonRealmRepositoryImpl<M extends RealmObject & RealmModel>
        implements CommonRealmRepository<M> {

    private static final Logger log = LoggerFactory.getLogger(CommonRealmRepositoryImpl.class);

    @Inject
    DatabaseHelper databaseHelper;

    protected CommonRealmRepositoryImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    protected abstract Class<M> getRealmModelClass();

    @Override
    public Observable<String> add(M model) {
        return Observable.create(subscriber -> {
            try {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.copyToRealm(model);
                realm.commitTransaction();

                subscriber.onNext(model.getUuid());
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("ADD", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Void> update(M model) {
        return Observable.create(subscriber -> {
            try {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(model);
                realm.commitTransaction();

                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("UPDATE", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Void> delete(String uuid) {
        return Observable.create(subscriber -> {
            try {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<M> models = realm.where(getRealmModelClass())
                    .equalTo(RealmModel.FIELD_UUID, uuid).findAll();

                realm.beginTransaction();
                models.clear();
                realm.commitTransaction();

                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("DELETE", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<M> findById(String uuid) {
        return Observable.create(subscriber -> {
            try {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<M> models = realm.where(getRealmModelClass())
                    .equalTo(RealmModel.FIELD_UUID, uuid).findAll();

                checkState(models.size() == 1, "primary key violation: invalid uuid");

                subscriber.onNext(models.get(0));
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("FIND_BY_ID", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<List<M>> findAll() {
        return Observable.create(subscriber -> {
            try {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<M> models = realm.where(getRealmModelClass()).findAll();

                subscriber.onNext(models);
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("FIND_ALL", e);
                subscriber.onError(e);
            }
        });
    }

}
