package com.github.openwebnet.model;

import com.github.openwebnet.model.firestore.FirestoreModel;
import com.github.openwebnet.model.firestore.ProfileVersionModel;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static com.google.common.base.Preconditions.checkNotNull;

public class EnvironmentModel extends RealmObject
        implements FirestoreModel<EnvironmentModel> {

    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";

    @Required
    @PrimaryKey
    private Integer id;

    @Required
    private String name;

    public static EnvironmentModel newInstance(Map<String, Object> map, ProfileVersionModel version) {
        return new EnvironmentModel().fromMap(map, version);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ID, getId());
        map.put(FIELD_NAME, getName());
        return map;
    }

    @Override
    public EnvironmentModel fromMap(Map<String, Object> map, ProfileVersionModel version) {
        checkNotNull(map.get(FIELD_ID), "id is null");
        checkNotNull(map.get(FIELD_NAME), "name is null");

        EnvironmentModel model = new EnvironmentModel();
        model.setId(((Long) map.get(FIELD_ID)).intValue());
        model.setName((String) map.get(FIELD_NAME));
        return model;
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
