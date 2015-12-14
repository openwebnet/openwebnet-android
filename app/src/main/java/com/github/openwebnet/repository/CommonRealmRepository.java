package com.github.openwebnet.repository;

import com.github.openwebnet.model.RealmModel;

import java.util.List;

import rx.Observable;

public interface CommonRealmRepository<M extends RealmModel> {

    Observable<String> add(M model);

    Observable<List<M>> findAll();
}
