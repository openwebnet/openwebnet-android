package com.github.openwebnet.repository.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.database.DatabaseRealm;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.repository.LightRepository;

import javax.inject.Inject;

public class LightRepositoryImpl extends DomoticRepositoryImpl<LightModel>
        implements LightRepository {

    @Inject
    DatabaseRealm databaseRealm;

    public LightRepositoryImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    protected Class<LightModel> getRealmModelClass() {
        return LightModel.class;
    }

}
