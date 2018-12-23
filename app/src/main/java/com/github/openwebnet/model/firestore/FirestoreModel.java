package com.github.openwebnet.model.firestore;

import java.util.Map;

public interface FirestoreModel<T> {

    Map<String, Object> toMap();

    T fromMap(Map<String, Object> map);

}
