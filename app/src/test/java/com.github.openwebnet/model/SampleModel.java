package com.github.openwebnet.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SampleModel extends RealmObject implements RealmModel {

    @PrimaryKey
    private String uuid;

    private String value;

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
