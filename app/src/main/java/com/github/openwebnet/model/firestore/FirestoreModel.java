package com.github.openwebnet.model.firestore;

import java.util.Map;

/*
 * CHANGELOG
 *
 * v3 @ 2019-02-18
 * > databaseFirestoreVersion 3
 * > databaseRealmVersion 10
 *   + add UserModel.iso3Language
 *   + add UserModel.iso3Country (not used)
 *   + add UserModel.locale (not used)
 *
 * v4 @ 2019-03-02
 * > databaseFirestoreVersion 4
 * > databaseRealmVersion 11
 *   + add AutomationModel.type
 *   + add AutomationModel.bus
 *   + add LightModel.bus
 */
public interface FirestoreModel<T> {

    int DATABASE_VERSION = 4;

    Map<String, Object> toMap();

    T fromMap(Map<String, Object> map, ProfileVersionModel version);

}
