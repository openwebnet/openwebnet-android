package com.github.openwebnet.repository.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.database.DatabaseRealm;
import com.github.openwebnet.model.AutomationModel;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.repository.AutomationRepository;
import com.github.openwebnet.repository.LightRepository;

import javax.inject.Inject;

public class AutomationRepositoryImpl extends DomoticRepositoryImpl<AutomationModel>
        implements AutomationRepository {

    @Inject
    DatabaseRealm databaseRealm;

    public AutomationRepositoryImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    protected Class<AutomationModel> getRealmModelClass() {
        return AutomationModel.class;
    }

}
