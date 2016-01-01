package com.github.openwebnet.model;

/**
 * From https://realm.io/docs/java/latest
 *
 * It is currently not possible to extend anything else
 * than RealmObject or to override methods.
 */
public interface RealmModel {

    String FIELD_UUID = "uuid";

    String getUuid();

}
