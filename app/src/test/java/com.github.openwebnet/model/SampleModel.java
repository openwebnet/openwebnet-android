package com.github.openwebnet.model;

import io.realm.RealmObject;

public class SampleModel extends RealmObject implements RealmModel {

    private String uuid;

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
