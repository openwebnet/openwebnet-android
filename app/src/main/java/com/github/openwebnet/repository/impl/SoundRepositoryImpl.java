package com.github.openwebnet.repository.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.database.DatabaseRealm;
import com.github.openwebnet.model.SoundModel;
import com.github.openwebnet.repository.SoundRepository;

import javax.inject.Inject;

public class SoundRepositoryImpl extends DomoticRepositoryImpl<SoundModel>
        implements SoundRepository {

    @Inject
    DatabaseRealm databaseRealm;

    public SoundRepositoryImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    protected Class<SoundModel> getRealmModelClass() {
        return SoundModel.class;
    }

}
