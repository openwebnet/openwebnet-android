package com.github.openwebnet.model.firestore;

import java.util.Map;

public interface FirestoreModel<T> {

    int DATABASE_VERSION = 2;

    Map<String, Object> toMap();

    T fromMap(Map<String, Object> map, ProfileVersionModel version);

}
