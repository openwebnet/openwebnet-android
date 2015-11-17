package com.github.openwebnet.repository.impl;

import com.github.openwebnet.model.DomoticEnvironment;
import com.github.openwebnet.repository.RepositoryDomoticEnvironment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import rx.Observable;

/**
 * @author niqdev
 */
public class RepositoryDomoticEnvironmentImpl implements RepositoryDomoticEnvironment {

    private static final Logger log = LoggerFactory.getLogger(RepositoryDomoticEnvironmentImpl.class);
    private final Realm realm;

    public RepositoryDomoticEnvironmentImpl(Realm realm) {
        this.realm = realm;
    }

    @Override
    public Observable<String> add(DomoticEnvironment environment) {
        // TODO observable
        String uuid = UUID.randomUUID().toString();
        environment.setUuid(uuid);

        realm.executeTransaction(r -> {
            r.copyToRealm(environment);
        });

        log.debug("Environment-ADD");
        return null;
    }

    @Override
    public Observable<DomoticEnvironment> find(String uuid) {
        log.debug("Environment-FIND");
        return null;
    }

    @Override
    public Observable<List<DomoticEnvironment>> findAll() {
        log.debug("Environment-FIND-ALL");
        return null;
    }
}
