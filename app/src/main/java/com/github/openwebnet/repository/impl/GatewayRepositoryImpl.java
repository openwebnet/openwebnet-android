package com.github.openwebnet.repository.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.repository.DatabaseRealm;
import com.github.openwebnet.repository.GatewayRepository;

import javax.inject.Inject;

public class GatewayRepositoryImpl extends CommonRealmRepositoryImpl<GatewayModel>
        implements GatewayRepository {

    @Inject
    DatabaseRealm databaseRealm;

    public GatewayRepositoryImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    protected Class<GatewayModel> getRealmModelClass() {
        return GatewayModel.class;
    }
}
