package com.github.openwebnet.repository;

import com.github.openwebnet.model.DomoticEnvironment;

import java.util.List;

import rx.Observable;

/**
 * @author niqdev
 */
public interface RepositoryDomoticEnvironment {

    Observable<String> add(DomoticEnvironment environment);

    Observable<DomoticEnvironment> find(String uuid);

    Observable<List<DomoticEnvironment>> findAll();

}
