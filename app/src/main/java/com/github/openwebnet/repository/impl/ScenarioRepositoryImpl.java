package com.github.openwebnet.repository.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.database.DatabaseRealm;
import com.github.openwebnet.model.ScenarioModel;
import com.github.openwebnet.repository.ScenarioRepository;

import javax.inject.Inject;

public class ScenarioRepositoryImpl extends DomoticRepositoryImpl<ScenarioModel>
        implements ScenarioRepository {

    @Inject
    DatabaseRealm databaseRealm;

    public ScenarioRepositoryImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    protected Class<ScenarioModel> getRealmModelClass() {
        return ScenarioModel.class;
    }
}
