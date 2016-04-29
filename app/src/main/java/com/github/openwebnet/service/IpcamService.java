package com.github.openwebnet.service;

import com.github.openwebnet.model.IpcamModel;

import java.util.List;

import rx.Observable;

public interface IpcamService {

    Observable<String> add(IpcamModel ipcam);

    Observable<Void> update(IpcamModel ipcam);

    Observable<Void> delete(String uuid);

    Observable<IpcamModel> findById(String uuid);

    Observable<List<IpcamModel>> findByEnvironment(Integer id);

    Observable<List<IpcamModel>> findFavourites();

}
