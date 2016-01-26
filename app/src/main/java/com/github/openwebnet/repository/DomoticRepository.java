package com.github.openwebnet.repository;

import com.github.openwebnet.model.DomoticModel;
import com.github.openwebnet.model.RealmModel;

import java.util.List;

import io.realm.RealmObject;
import rx.Observable;

public interface DomoticRepository<D extends RealmObject & RealmModel & DomoticModel>
        extends CommonRealmRepository<D> {

    Observable<List<D>> findByEnvironment(Integer id);

    Observable<List<D>> findFavourites();

}
