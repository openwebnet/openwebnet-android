package com.github.openwebnet.service.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.EnergyModel;
import com.github.openwebnet.repository.EnergyRepository;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.EnergyService;
import com.github.openwebnet.service.PreferenceService;
import com.github.openwebnet.service.UtilityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class EnergyServiceImpl implements EnergyService {

    private static final Logger log = LoggerFactory.getLogger(EnergyService.class);

    @Inject
    EnergyRepository energyRepository;

    @Inject
    CommonService commonService;

    @Inject
    PreferenceService preferenceService;

    @Inject
    UtilityService utilityService;

    public EnergyServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public Observable<String> add(EnergyModel energy) {
        return energyRepository.add(energy);
    }

    @Override
    public Observable<Void> update(EnergyModel energy) {
        return energyRepository.update(energy);
    }

    @Override
    public Observable<Void> delete(String uuid) {
        return energyRepository.delete(uuid);
    }

    @Override
    public Observable<EnergyModel> findById(String uuid) {
        return energyRepository.findById(uuid);
    }

    @Override
    public Observable<List<EnergyModel>> findByEnvironment(Integer id) {
        return energyRepository.findByEnvironment(id);
    }

    @Override
    public Observable<List<EnergyModel>> findFavourites() {
        return energyRepository.findFavourites();
    }

    @Override
    public Observable<List<EnergyModel>> requestByEnvironment(Integer id) {
        return null;
    }

    @Override
    public Observable<List<EnergyModel>> requestFavourites() {
        return null;
    }

}
