package com.github.openwebnet.repository.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.database.DatabaseRealm;
import com.github.openwebnet.model.IpcamModel;
import com.github.openwebnet.repository.IpcamRepository;

import javax.inject.Inject;

public class IpcamRepositoryImpl extends DomoticRepositoryImpl<IpcamModel>
        implements IpcamRepository {

    @Inject
    DatabaseRealm databaseRealm;

    public IpcamRepositoryImpl() {
        Injector.getApplicationComponent().inject(this);
    }
    
    @Override
    protected Class<IpcamModel> getRealmModelClass() {
        return IpcamModel.class;
    }
}
