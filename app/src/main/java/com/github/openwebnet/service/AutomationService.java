package com.github.openwebnet.service;

import com.github.openwebnet.model.AutomationModel;
import com.github.openwebnet.model.LightModel;

import java.util.List;

import rx.Observable;

public interface AutomationService {

    Observable<String> add(AutomationModel automation);

    Observable<Void> update(AutomationModel automation);

    Observable<Void> delete(String uuid);

    Observable<AutomationModel> findById(String uuid);

    Observable<List<AutomationModel>> findByEnvironment(Integer id);

    Observable<List<AutomationModel>> findFavourites();

    /* operate on background threads */

    Observable<List<AutomationModel>> requestByEnvironment(Integer id);

    Observable<List<AutomationModel>> requestFavourites();

    Observable<AutomationModel> stop(AutomationModel automation);

    Observable<AutomationModel> moveUp(AutomationModel automation);

    Observable<AutomationModel> moveDown(AutomationModel automation);

}
