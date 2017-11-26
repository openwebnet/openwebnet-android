package com.github.openwebnet.service;

import com.github.openwebnet.model.EnergyModel;

import java.util.List;

import rx.Observable;

public interface EnergyService extends DomoticService<EnergyModel> {

    Observable<List<EnergyModel>> requestByEnvironment(Integer id);

    Observable<List<EnergyModel>> requestFavourites();

}
