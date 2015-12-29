package com.github.openwebnet.service;

import com.github.openwebnet.model.LightModel;

import java.util.List;

import rx.Observable;

public interface LightService {

    Observable<String> add(LightModel.Builder light);

    Observable<List<LightModel>> findAll();

    Observable<List<LightModel>> findByEnvironment(Integer id);

    Observable<Void> update(LightModel light);

}
