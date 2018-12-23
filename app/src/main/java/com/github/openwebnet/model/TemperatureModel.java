package com.github.openwebnet.model;

import com.github.openwebnet.model.firestore.FirestoreModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static com.google.common.base.Preconditions.checkNotNull;

public class TemperatureModel extends RealmObject
        implements RealmModel, DomoticModel, FirestoreModel<TemperatureModel> {

    @Required
    @PrimaryKey
    private String uuid;

    @Required
    private Integer environmentId;

    @Required
    private String gatewayUuid;

    @Required
    private String name;

    @Required
    private String where;

    private boolean favourite;

    @Ignore
    private String value;

    public TemperatureModel() {}

    private TemperatureModel(Builder builder) {
        this.uuid = builder.uuid;
        this.environmentId = builder.environmentId;
        this.gatewayUuid = builder.gatewayUuid;
        this.name = builder.name;
        this.where = builder.where;
        this.favourite = builder.favourite;
    }

    public static class Builder {

        private final String uuid;
        private Integer environmentId;
        private String gatewayUuid;
        private String name;
        private String where;
        private boolean favourite;

        public Builder(String uuid) {
            this.uuid = uuid;
        }

        public Builder(Map<String, Object> map) {
            this.uuid = (String) map.get(FIELD_UUID);
            this.environmentId = (Integer) map.get(FIELD_ENVIRONMENT_ID);
            this.gatewayUuid = (String) map.get(FIELD_GATEWAY_UUID);
            this.name = (String) map.get(FIELD_NAME);
            this.where = (String) map.get(FIELD_WHERE);
            this.favourite = (Boolean) map.get(FIELD_FAVOURITE);
        }

        public Builder environment(Integer environmentId) {
            this.environmentId = environmentId;
            return this;
        }

        public Builder gateway(String gatewayUuid) {
            this.gatewayUuid = gatewayUuid;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder where(String where) {
            this.where = where;
            return this;
        }

        public Builder favourite(boolean favourite) {
            this.favourite = favourite;
            return this;
        }

        public TemperatureModel build() {
            checkNotNull(environmentId, "environmentId is null");
            checkNotNull(gatewayUuid, "gatewayUuid is null");
            checkNotNull(name, "name is null");
            checkNotNull(where, "where is null");

            return new TemperatureModel(this);
        }
    }

    public static Builder addBuilder() {
        return new Builder(UUID.randomUUID().toString());
    }

    public static Builder updateBuilder(String uuid) {
        return new Builder(uuid);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_UUID, getUuid());
        map.put(FIELD_ENVIRONMENT_ID, getEnvironmentId());
        map.put(FIELD_GATEWAY_UUID, getGatewayUuid());
        map.put(FIELD_NAME, getName());
        map.put(FIELD_WHERE, getWhere());
        map.put(FIELD_FAVOURITE, isFavourite());
        return map;
    }

    @Override
    public TemperatureModel fromMap(Map<String, Object> map) {
        return new Builder(map).build();
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public Integer getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Integer environmentId) {
        this.environmentId = environmentId;
    }

    @Override
    public String getGatewayUuid() {
        return gatewayUuid;
    }

    public void setGatewayUuid(String gatewayUuid) {
        this.gatewayUuid = gatewayUuid;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    @Override
    public boolean isFavourite() {
        return favourite;
    }

    @Override
    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
