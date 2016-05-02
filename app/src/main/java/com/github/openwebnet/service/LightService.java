package com.github.openwebnet.service;

import com.github.openwebnet.model.LightModel;

import java.util.List;

import rx.Observable;

public interface LightService extends DomoticService<LightModel> {

    Observable<List<LightModel>> requestByEnvironment(Integer id);

    Observable<List<LightModel>> requestFavourites();

    Observable<LightModel> turnOn(LightModel light);

    Observable<LightModel> turnOff(LightModel light);

}
