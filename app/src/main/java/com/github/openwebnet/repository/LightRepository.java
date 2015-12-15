package com.github.openwebnet.repository;

import com.github.openwebnet.model.LightModel;

import java.util.List;

import rx.Observable;

public interface LightRepository extends CommonRealmRepository<LightModel> {

    Observable<List<LightModel>> findByEnvironment(Integer id);

}
