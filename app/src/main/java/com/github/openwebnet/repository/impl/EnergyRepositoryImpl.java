package com.github.openwebnet.repository.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.database.DatabaseRealm;
import com.github.openwebnet.model.EnergyModel;
import com.github.openwebnet.repository.EnergyRepository;

import javax.inject.Inject;

public class EnergyRepositoryImpl extends DomoticRepositoryImpl<EnergyModel>
    implements EnergyRepository {

    @Inject
    DatabaseRealm databaseRealm;

    public EnergyRepositoryImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    protected Class<EnergyModel> getRealmModelClass() {
        return EnergyModel.class;
    }

}
