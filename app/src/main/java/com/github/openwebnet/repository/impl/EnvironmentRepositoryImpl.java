package com.github.openwebnet.repository.impl;

import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.repository.EnvironmentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;

public class EnvironmentRepositoryImpl implements EnvironmentRepository {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentRepository.class);
    private static final Integer INITIAL_SEQ = 100;

    @Override
    public Observable<Integer> getNextId() {
        return Observable.create(subscriber -> {
            try {
                Number maxId = Realm.getDefaultInstance().where(EnvironmentModel.class).max("id");
                Integer lastId = maxId == null ? INITIAL_SEQ : new AtomicInteger(maxId.intValue()).incrementAndGet();
                subscriber.onNext(lastId);
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("environment-NEXT_ID", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Integer> add(EnvironmentModel environment) {
        return Observable.create(subscriber -> {
            try {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.copyToRealm(environment);
                realm.commitTransaction();

                subscriber.onNext(environment.getId());
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("environment-ADD", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<EnvironmentModel> find(Integer id) {
        log.debug("environment-FIND");
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Observable<List<EnvironmentModel>> findAll() {
        return Observable.create(subscriber -> {
            try {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<EnvironmentModel> environments = realm.where(EnvironmentModel.class).findAll();
                environments.sort(EnvironmentModel.FIELD_NAME, Sort.ASCENDING);

                subscriber.onNext(realm.copyFromRealm(environments));
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("environment-FIND_ALL", e);
                subscriber.onError(e);
            }
        });
    }

}
