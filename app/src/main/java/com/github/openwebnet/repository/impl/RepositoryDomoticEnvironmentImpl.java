package com.github.openwebnet.repository.impl;

import com.github.openwebnet.model.DomoticEnvironment;
import com.github.openwebnet.repository.RepositoryDomoticEnvironment;

import java.util.List;
import java.util.UUID;

import rx.Observable;

/**
 * @author niqdev
 */
public class RepositoryDomoticEnvironmentImpl implements RepositoryDomoticEnvironment {

    @Override
    public Observable<String> add(DomoticEnvironment environment) {
        environment.setUuid(UUID.randomUUID().toString());
        return null;
    }

    @Override
    public Observable<DomoticEnvironment> find(String uuid) {
        return null;
    }

    @Override
    public Observable<List<DomoticEnvironment>> findAll() {
        return null;
    }
}
