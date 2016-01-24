package com.github.openwebnet.service;

import com.github.openwebnet.model.EnvironmentModel;

import java.util.List;

import rx.Observable;

public interface EnvironmentService {

    Observable<Integer> add(String name);

    Observable<Void> update(EnvironmentModel environment);

    Observable<List<EnvironmentModel>> findAll();

    Observable<EnvironmentModel> findById(Integer id);

}
