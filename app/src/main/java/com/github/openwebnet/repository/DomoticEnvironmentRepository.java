package com.github.openwebnet.repository;

import com.github.openwebnet.model.DomoticEnvironment;

import java.util.List;

import rx.Observable;

/**
 * @author niqdev
 */
public interface DomoticEnvironmentRepository {

    Observable<Integer> getNextId();

    Observable<Integer> add(DomoticEnvironment environment);

    Observable<DomoticEnvironment> find(String uuid);

    Observable<List<DomoticEnvironment>> findAll();

}
