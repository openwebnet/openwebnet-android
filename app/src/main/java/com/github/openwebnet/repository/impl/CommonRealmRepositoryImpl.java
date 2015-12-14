package com.github.openwebnet.repository.impl;

import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.repository.CommonRealmRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;

public abstract class CommonRealmRepositoryImpl<M extends RealmModel> implements CommonRealmRepository<M> {

    private static final Logger log = LoggerFactory.getLogger(CommonRealmRepositoryImpl.class);

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
    public Observable<List<M>> findAll() {
        return Observable.create(subscriber -> {
            try {
                RealmResults<M> models =
                    Realm.getDefaultInstance().where(getRealmModelClass()).findAll();
                subscriber.onNext(models);
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("FIND_ALL", e);
                subscriber.onError(e);
            }
        });
    }
}
