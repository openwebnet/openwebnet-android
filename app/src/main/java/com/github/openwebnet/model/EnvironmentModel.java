package com.github.openwebnet.model;

import com.github.openwebnet.model.firestore.FirestoreModel;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class EnvironmentModel extends RealmObject implements FirestoreModel {

    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";

    @Required
    @PrimaryKey
    private Integer id;

    @Required
    private String name;

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ID, getId());
        map.put(FIELD_NAME, getName());
        return map;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
