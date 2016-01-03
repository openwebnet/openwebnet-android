package com.github.openwebnet.repository;

import com.github.openwebnet.model.RealmModel;

import java.util.List;

import io.realm.RealmObject;
import rx.Observable;

public interface CommonRealmRepository<M extends RealmObject & RealmModel> {

    Observable<String> add(M model);

    Observable<Void> update(M model);

    Observable<Void> delete(String uuid);

    Observable<M> findById(String uuid);

    Observable<List<M>> findAll();

}
