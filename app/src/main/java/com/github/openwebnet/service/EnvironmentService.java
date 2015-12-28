package com.github.openwebnet.service;

import com.github.openwebnet.model.EnvironmentModel;

import java.util.List;

import rx.Observable;

public interface EnvironmentService {

    Observable<Integer> addEnvironment(String name);

    Observable<List<EnvironmentModel>> findAllEnvironment();

}
