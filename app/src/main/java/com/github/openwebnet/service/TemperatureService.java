package com.github.openwebnet.service;

import com.github.openwebnet.model.TemperatureModel;

import java.util.List;

import rx.Observable;

public interface TemperatureService extends DomoticService<TemperatureModel> {

    Observable<List<TemperatureModel>> requestByEnvironment(Integer id);

    Observable<List<TemperatureModel>> requestFavourites();

}
