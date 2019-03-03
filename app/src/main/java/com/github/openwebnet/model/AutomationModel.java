package com.github.openwebnet.model;

import com.github.niqdev.openwebnet.message.Automation;
import com.github.openwebnet.model.firestore.FirestoreModel;
import com.github.openwebnet.model.firestore.ProfileVersionModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static com.google.common.base.Preconditions.checkNotNull;

public class AutomationModel extends RealmObject
        implements RealmModel, DomoticModel, FirestoreModel<AutomationModel> {

    public static final String FIELD_TYPE = "type";
    public static final String FIELD_BUS = "bus";

    public enum Status {
        STOP, UP, DOWN
    }

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

    @Required
    private String type;

    @Required
    private String bus;

    private boolean favourite;

    @Ignore
    private Status status;

    // NOT USED: realm error otherwise
    @Ignore
    private Automation.Type automationType;

    public AutomationModel() {}

    private AutomationModel(Builder builder) {
        this.uuid = builder.uuid;
        this.environmentId = builder.environmentId;
        this.gatewayUuid = builder.gatewayUuid;
        this.name = builder.name;
        this.where = builder.where;
        this.type = builder.type;
        this.bus = builder.bus;
        this.favourite = builder.favourite;
    }

    public static class Builder {

        private final String uuid;
        private Integer environmentId;
        private String gatewayUuid;
        private String name;
        private String where;
        private String type;
        private String bus;
        private boolean favourite;

        private Builder(String uuid) {
            this.uuid = uuid;
        }

        private Builder(Map<String, Object> map) {
            this.uuid = (String) map.get(FIELD_UUID);
            this.environmentId = ((Long) map.get(FIELD_ENVIRONMENT_ID)).intValue();
            this.gatewayUuid = (String) map.get(FIELD_GATEWAY_UUID);
            this.name = (String) map.get(FIELD_NAME);
            this.where = (String) map.get(FIELD_WHERE);
            this.type = (String) map.get(FIELD_TYPE);
            this.bus = (String) map.get(FIELD_BUS);
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

        public Builder type(Automation.Type type) {
            this.type = type.name();
            return this;
        }

        public Builder bus(String bus) {
            this.bus = bus;
            return this;
        }

        public Builder favourite(boolean favourite) {
            this.favourite = favourite;
            return this;
        }

        public AutomationModel build() {
            checkNotNull(environmentId, "environmentId is null");
            checkNotNull(gatewayUuid, "gatewayUuid is null");
            checkNotNull(name, "name is null");
            checkNotNull(where, "where is null");
            checkNotNull(type, "type is null");
            checkNotNull(bus, "bus is null");

            return new AutomationModel(this);
        }
    }

    public static Builder addBuilder() {
        return new Builder(UUID.randomUUID().toString());
    }

    public static Builder updateBuilder(String uuid) {
        return new Builder(uuid);
    }

    public static AutomationModel newInstance(Map<String, Object> map, ProfileVersionModel version) {
        if (version.getDatabaseFirestoreVersion() < 4) {
            map.put(FIELD_TYPE, Automation.Type.POINT.name());
            map.put(FIELD_BUS, Automation.NO_BUS);
        }
        return new AutomationModel().fromMap(map, version);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_UUID, getUuid());
        map.put(FIELD_ENVIRONMENT_ID, getEnvironmentId());
        map.put(FIELD_GATEWAY_UUID, getGatewayUuid());
        map.put(FIELD_NAME, getName());
        map.put(FIELD_WHERE, getWhere());
        map.put(FIELD_TYPE, getAutomationType());
        map.put(FIELD_BUS, getBus());
        map.put(FIELD_FAVOURITE, isFavourite());
        return map;
    }

    @Override
    public AutomationModel fromMap(Map<String, Object> map, ProfileVersionModel version) {
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

    public String getType() {
        throw new UnsupportedOperationException("method not implemented: use AutomationModel#getAutomationType()");
    }

    public void setType(String type) {
        throw new UnsupportedOperationException("method not implemented: use AutomationModel#setAutomationType()");
    }

    public String getBus() {
        return bus;
    }

    public void setBus(String bus) {
        this.bus = bus;
    }

    @Override
    public boolean isFavourite() {
        return favourite;
    }

    @Override
    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Automation.Type getAutomationType() {
        return Automation.Type.valueOf(this.type);
    }

    public void setAutomationType(Automation.Type automationType) {
        this.type = automationType.name();
    }

}
