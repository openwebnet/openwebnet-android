package com.github.openwebnet.repository.impl;

import com.github.openwebnet.model.DomoticEnvironment;
import com.github.openwebnet.repository.RepositoryDomoticEnvironment;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.Observable;

/**
 * @author niqdev
 */
public class RepositoryDomoticEnvironmentImpl implements RepositoryDomoticEnvironment {

    private static final Logger log = LoggerFactory.getLogger(RepositoryDomoticEnvironmentImpl.class);

    @Override
    public Observable<String> add(DomoticEnvironment environment) {
        return Observable.create(subscriber -> {
            try {
                Realm realm  = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.copyToRealm(environment);
                realm.commitTransaction();

                subscriber.onNext(environment.getUuid());
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("environment-ADD", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<DomoticEnvironment> find(String uuid) {
        log.debug("environment-FIND");
        return null;
    }

    @Override
    public Observable<List<DomoticEnvironment>> findAll() {
        return Observable.create(subscriber -> {
            try {
                RealmResults<DomoticEnvironment> environments =
                    Realm.getDefaultInstance().where(DomoticEnvironment.class).findAll();
                environments.sort(DomoticEnvironment.NAME, RealmResults.SORT_ORDER_ASCENDING);

                // from documentation: https://realm.io/docs/java/latest/#queries
                // 'Most queries in Realm are fast enough to be run synchronously - even on the UI thread'
                // so avoid deep copy and schedulers
                subscriber.onNext(environments);
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("environment-FIND_ALL", e);
                subscriber.onError(e);
            }
        });
    }
}
