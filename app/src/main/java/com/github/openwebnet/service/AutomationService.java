package com.github.openwebnet.service;

import com.github.openwebnet.model.AutomationModel;

import java.util.List;

import rx.Observable;

public interface AutomationService extends DomoticService<AutomationModel> {

    Observable<List<AutomationModel>> requestByEnvironment(Integer id);

    Observable<List<AutomationModel>> requestFavourites();

    Observable<AutomationModel> stop(AutomationModel automation);

    Observable<AutomationModel> moveUp(AutomationModel automation);

    Observable<AutomationModel> moveDown(AutomationModel automation);

}
