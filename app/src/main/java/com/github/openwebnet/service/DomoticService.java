package com.github.openwebnet.service;

import com.github.openwebnet.model.DomoticEnvironment;

import java.util.List;

import rx.Observable;

public interface DomoticService {

    void initRepository();

    Observable<Integer> addEnvironment(String name, String description);

    Observable<List<DomoticEnvironment>> findAllEnvironment();
}
