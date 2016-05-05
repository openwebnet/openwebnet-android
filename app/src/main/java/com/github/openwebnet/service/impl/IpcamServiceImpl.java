package com.github.openwebnet.service.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.IpcamModel;
import com.github.openwebnet.repository.IpcamRepository;
import com.github.openwebnet.service.IpcamService;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class IpcamServiceImpl implements IpcamService {

    @Inject
    IpcamRepository ipcamRepository;

    public IpcamServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public Observable<String> add(IpcamModel ipcam) {
        return ipcamRepository.add(ipcam);
    }

    @Override
    public Observable<Void> update(IpcamModel ipcam) {
        return ipcamRepository.update(ipcam);
    }

    @Override
    public Observable<Void> delete(String uuid) {
        return ipcamRepository.delete(uuid);
    }

    @Override
    public Observable<IpcamModel> findById(String uuid) {
        return ipcamRepository.findById(uuid);
    }

    @Override
    public Observable<List<IpcamModel>> findByEnvironment(Integer id) {
        return ipcamRepository.findByEnvironment(id);
    }

    @Override
    public Observable<List<IpcamModel>> findFavourites() {
        return ipcamRepository.findFavourites();
    }

}
