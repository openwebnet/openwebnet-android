package com.github.openwebnet.repository.impl;

import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.repository.GatewayRepository;

public class GatewayRepositoryImpl extends CommonRealmRepositoryImpl<GatewayModel>
        implements GatewayRepository {

    @Override
    protected Class<GatewayModel> getRealmModelClass() {
        return GatewayModel.class;
    }
}
