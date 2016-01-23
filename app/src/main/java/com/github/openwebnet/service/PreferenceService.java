package com.github.openwebnet.service;

public interface PreferenceService {

    boolean isFirstRun();

    void initFirstRun();

    String getDefaultGateway();
}
