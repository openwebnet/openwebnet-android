package com.github.openwebnet.repository.impl;

import com.github.openwebnet.model.DomoticModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.repository.DomoticRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.realm.RealmObject;
import rx.Observable;

public abstract class DomoticRepositoryImpl<D extends RealmObject & RealmModel & DomoticModel>
        extends CommonRealmRepositoryImpl<D> implements DomoticRepository<D> {

    private static final Logger log = LoggerFactory.getLogger(DomoticRepository.class);

    @Override
    public Observable<List<D>> findByEnvironment(Integer id) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(databaseRealm
                    .findCopyWhere(getRealmModelClass(), DomoticModel.FIELD_ENVIRONMENT_ID, id));
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("FIND_BY_ENVIRONMENT", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<List<D>> findFavourites() {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(databaseRealm
                    .findCopyWhere(getRealmModelClass(), DomoticModel.FIELD_FAVOURITE, true));
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("FIND_FAVOURITES", e);
                subscriber.onError(e);
            }
        });
    }
}
