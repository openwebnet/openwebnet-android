package com.github.openwebnet.repository.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.database.DatabaseRealm;
import com.github.openwebnet.model.ScenarioModel;
import com.github.openwebnet.repository.ScenerioRepository;

import javax.inject.Inject;

public class ScenerioRepositoryImpl extends DomoticRepositoryImpl<ScenarioModel>
        implements ScenerioRepository {

    @Inject
    DatabaseRealm databaseRealm;

    public ScenerioRepositoryImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    protected Class<ScenarioModel> getRealmModelClass() {
        return ScenarioModel.class;
    }
}
