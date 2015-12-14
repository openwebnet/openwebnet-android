package com.github.openwebnet.model;

import io.realm.RealmObject;

public abstract class RealmModel extends RealmObject {

    public abstract String getUuid();

}
