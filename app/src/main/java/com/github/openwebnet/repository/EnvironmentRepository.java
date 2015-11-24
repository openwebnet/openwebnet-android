package com.github.openwebnet.repository;

import com.github.openwebnet.model.EnvironmentModel;

import java.util.List;

import rx.Observable;

public interface EnvironmentRepository {

    Observable<Integer> getNextId();

    Observable<Integer> add(EnvironmentModel environment);

    Observable<EnvironmentModel> find(Integer id);

    Observable<List<EnvironmentModel>> findAll();
    
}
