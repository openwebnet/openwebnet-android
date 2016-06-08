package com.github.openwebnet.repository.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.database.DatabaseRealm;
import com.github.openwebnet.model.TemperatureModel;
import com.github.openwebnet.repository.TemperatureRepository;

import javax.inject.Inject;

public class TemperatureRepositoryImpl extends DomoticRepositoryImpl<TemperatureModel>
    implements TemperatureRepository{

    @Inject
    DatabaseRealm databaseRealm;

    public TemperatureRepositoryImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    protected Class<TemperatureModel> getRealmModelClass() {
        return TemperatureModel.class;
    }

}
