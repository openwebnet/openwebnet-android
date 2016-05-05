package com.github.openwebnet.service;

import com.github.openwebnet.model.DomoticModel;

import java.util.List;

import rx.Observable;

public interface DomoticService<D extends DomoticModel> {

    Observable<String> add(D automation);

    Observable<Void> update(D automation);

    Observable<Void> delete(String uuid);

    Observable<D> findById(String uuid);

    Observable<List<D>> findByEnvironment(Integer id);

    Observable<List<D>> findFavourites();

}
