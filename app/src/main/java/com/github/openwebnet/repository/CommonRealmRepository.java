package com.github.openwebnet.repository;

import com.github.openwebnet.model.RealmModel;

import java.util.List;

import io.realm.RealmObject;
import rx.Observable;

public interface CommonRealmRepository<M extends RealmObject & RealmModel> {

    Observable<String> add(M model);

    Observable<List<M>> findAll();

    Observable<Void> update(M model);
}
