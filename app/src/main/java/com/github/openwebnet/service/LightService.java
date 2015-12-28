package com.github.openwebnet.service;

import com.github.openwebnet.model.LightModel;

import java.util.List;

import rx.Observable;

public interface LightService {

    Observable<String> addLight(LightModel.Builder light);

    Observable<List<LightModel>> findAllLight();

    Observable<List<LightModel>> findLightByEnvironment(Integer id);

}
