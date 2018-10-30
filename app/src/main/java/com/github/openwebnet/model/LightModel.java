package com.github.openwebnet.model;

import com.github.niqdev.openwebnet.message.Lighting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static com.google.common.base.Preconditions.checkNotNull;

public class LightModel extends RealmObject implements RealmModel, DomoticModel {

    public static final String FIELD_TYPE = "type";

    public enum Status {
        ON, OFF
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

    // TODO remove
    @Deprecated
    private boolean dimmer;

    private boolean favourite;

    @Ignore
    private Status status;

    // NOT USED: realm error otherwise
    @Ignore
    private Lighting.Type lightingType;

    public LightModel() {}

    private LightModel(Builder builder) {
        this.uuid = builder.uuid;
        this.environmentId = builder.environmentId;
        this.gatewayUuid = builder.gatewayUuid;
        this.name = builder.name;
        this.where = builder.where;
        this.type = builder.type;
        this.dimmer = builder.dimmer;
        this.favourite = builder.favourite;
    }

    public static class Builder {

        private final String uuid;
        private Integer environmentId;
        private String gatewayUuid;
        private String name;
        private String where;
        private String type;
        private boolean dimmer;
        private boolean favourite;

        public Builder(String uuid) {
            this.uuid = uuid;
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

        public Builder type(Lighting.Type type) {
            this.type = type.name();
            return this;
        }

        public Builder dimmer(boolean dimmer) {
            this.dimmer = dimmer;
            return this;
        }

        public Builder favourite(boolean favourite) {
            this.favourite = favourite;
            return this;
        }

        public LightModel build() {
            checkNotNull(environmentId, "environmentId is null");
            checkNotNull(gatewayUuid, "gatewayUuid is null");
            checkNotNull(name, "name is null");
            checkNotNull(where, "where is null");
            checkNotNull(type, "type is null");

            return new LightModel(this);
        }
    }

    public static Builder addBuilder() {
        return new Builder(UUID.randomUUID().toString());
    }

    public static Builder updateBuilder(String uuid) {
        return new Builder(uuid);
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

    public String getType() {
        throw new UnsupportedOperationException("method not implemented: use LightModel#getLightingType()");
    }

    public void setType(String type) {
        throw new UnsupportedOperationException("method not implemented: use LightModel#setLightingType()");
    }

    @Deprecated
    public boolean isDimmer() {
        return dimmer;
    }

    @Deprecated
    public void setDimmer(boolean dimmer) {
        this.dimmer = dimmer;
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

    public Lighting.Type getLightingType() {
        return Lighting.Type.valueOf(this.type);
    }

    public void setLightingType(Lighting.Type lightingType) {
        this.type = lightingType.name();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_UUID, getUuid());
        map.put(FIELD_ENVIRONMENT_ID, getEnvironmentId());
        map.put(FIELD_GATEWAY_UUID, getGatewayUuid());
        map.put(FIELD_NAME, getName());
        map.put(FIELD_WHERE, getWhere());
        map.put(FIELD_TYPE, getLightingType());
        map.put(FIELD_FAVOURITE, isFavourite());
        return map;
    }

}
