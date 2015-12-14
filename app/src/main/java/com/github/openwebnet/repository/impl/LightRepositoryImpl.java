package com.github.openwebnet.repository.impl;

import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.repository.LightRepository;

public class LightRepositoryImpl extends CommonRealmRepositoryImpl<LightModel>
        implements LightRepository {

    @Override
    protected Class<LightModel> getRealmModelClass() {
        return LightModel.class;
    }
}
