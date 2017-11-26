package com.github.openwebnet.service.impl;

import com.github.niqdev.openwebnet.OpenSession;
import com.github.niqdev.openwebnet.message.EnergyManagement;
import com.github.niqdev.openwebnet.message.OpenMessage;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.EnergyModel;
import com.github.openwebnet.repository.EnergyRepository;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.EnergyService;
import com.github.openwebnet.service.PreferenceService;
import com.github.openwebnet.service.UtilityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static java.util.Arrays.asList;

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
        return findByEnvironment(id)
            .flatMapIterable(energyModels -> energyModels)
            .flatMap(requestPowerConsumption())
            .collect(ArrayList::new, List::add);
    }

    @Override
    public Observable<List<EnergyModel>> requestFavourites() {
        return findFavourites()
            .flatMapIterable(energyModels -> energyModels)
            .flatMap(requestPowerConsumption())
            .collect(ArrayList::new, List::add);
    }

    private Func1<EnergyModel, Observable<EnergyModel>> requestPowerConsumption() {

        final Func1<EnergyModel, List<OpenMessage>> requests = energy ->
            asList(
                EnergyManagement.requestInstantaneousPower(energy.getWhere(), energy.getEnergyManagementVersion()),
                EnergyManagement.requestDailyPower(energy.getWhere(), energy.getEnergyManagementVersion()),
                EnergyManagement.requestMonthlyPower(energy.getWhere(), energy.getEnergyManagementVersion()));

        final Func2<List<OpenSession>, EnergyModel, EnergyModel> handler = (openSessions, energy) -> {
            EnergyManagement.handlePowers(values -> {
                energy.setInstantaneousPower(values.get(0));
                energy.setDailyPower(values.get(1));
                energy.setMonthlyPower(values.get(2));
            }, () -> {
                energy.setInstantaneousPower(null);
                energy.setDailyPower(null);
                energy.setMonthlyPower(null);
            }).call(openSessions);
            return energy;
        };

        return energy -> commonService.findClient(energy.getGatewayUuid())
            .send(requests.call(energy))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(openSessions -> handler.call(openSessions, energy))
            .onErrorReturn(throwable -> {
                log.warn("energy={} | failing requests={}", energy.getUuid(), requests);
                // unreadable energy
                return energy;
            });

    }

}
