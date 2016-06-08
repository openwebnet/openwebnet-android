package com.github.openwebnet.service;

import com.github.openwebnet.model.DomoticModel;

import java.util.List;

import rx.Observable;

public interface DomoticService<D extends DomoticModel> {

    Observable<String> add(D item);

    Observable<Void> update(D item);

    Observable<Void> delete(String uuid);

    Observable<D> findById(String uuid);

    Observable<List<D>> findByEnvironment(Integer id);

    Observable<List<D>> findFavourites();

}
