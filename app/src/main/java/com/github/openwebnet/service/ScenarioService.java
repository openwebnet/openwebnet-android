package com.github.openwebnet.service;

import com.github.openwebnet.model.ScenarioModel;

import java.util.List;

import rx.Observable;

public interface ScenarioService extends DomoticService<ScenarioModel> {

    Observable<List<ScenarioModel>> requestByEnvironment(Integer id);

    Observable<List<ScenarioModel>> requestFavourites();

    Observable<ScenarioModel> start(ScenarioModel scenario);

    Observable<ScenarioModel> stop(ScenarioModel scenario);

}
