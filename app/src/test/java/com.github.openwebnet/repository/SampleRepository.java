package com.github.openwebnet.repository;

import com.github.openwebnet.component.ApplicationComponentTest;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.SampleModel;
import com.github.openwebnet.repository.impl.CommonRealmRepositoryImpl;

import javax.inject.Inject;

public class SampleRepository extends CommonRealmRepositoryImpl<SampleModel> {

    @Inject
    DatabaseRealm databaseRealm;

    public SampleRepository() {
        ((ApplicationComponentTest) Injector.getApplicationComponent()).inject(this);
    }


    @Override
    protected Class<SampleModel> getRealmModelClass() {
        return SampleModel.class;
    }

}
