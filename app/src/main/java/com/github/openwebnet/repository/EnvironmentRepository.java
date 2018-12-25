package com.github.openwebnet.repository;

import com.github.openwebnet.model.EnvironmentModel;

import java.util.List;

import rx.Observable;

public interface EnvironmentRepository {

    Observable<Integer> getNextId();

    Observable<Integer> add(EnvironmentModel environment);

    Observable<List<Integer>> addAll(List<EnvironmentModel> environments);

    Observable<Void> update(EnvironmentModel environment);

    Observable<EnvironmentModel> findById(Integer id);

    Observable<List<EnvironmentModel>> findAll();

    Observable<Void> delete(Integer id);

    Observable<Void> deleteAll();

}
