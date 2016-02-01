package com.github.openwebnet.repository.impl;

import com.github.openwebnet.model.DomoticModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.repository.DomoticRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.realm.RealmObject;
import rx.Observable;

import static com.github.openwebnet.model.DomoticModel.FIELD_ENVIRONMENT_ID;
import static com.github.openwebnet.model.DomoticModel.FIELD_FAVOURITE;
import static com.github.openwebnet.model.DomoticModel.FIELD_NAME;

public abstract class DomoticRepositoryImpl<D extends RealmObject & RealmModel & DomoticModel>
        extends CommonRealmRepositoryImpl<D> implements DomoticRepository<D> {

    private static final Logger log = LoggerFactory.getLogger(DomoticRepository.class);

    @Override
    public Observable<List<D>> findByEnvironment(Integer id) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(databaseRealm
                    .findCopyWhere(getRealmModelClass(), FIELD_ENVIRONMENT_ID, id, FIELD_NAME));
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("FIND_BY_ENVIRONMENT-orderByName", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<List<D>> findFavourites() {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(databaseRealm
                    .findCopyWhere(getRealmModelClass(), FIELD_FAVOURITE, true, FIELD_NAME));
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("FIND_FAVOURITES-orderByName", e);
                subscriber.onError(e);
            }
        });
    }
}
