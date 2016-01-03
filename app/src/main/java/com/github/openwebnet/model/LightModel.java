package com.github.openwebnet.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static java.util.Objects.requireNonNull;

public class LightModel extends RealmObject implements RealmModel, DomoticModel {

    public static final String FIELD_ENVIRONMENT_ID = "environmentId";

    public enum Status {
        ON, OFF
    }

    @PrimaryKey
    private String uuid;

    private Integer environmentId;

    private String gatewayUuid;

    @Required
    private String name;

    @Required
    private Integer where;

    private boolean dimmer;

    private boolean favourite;

    @Ignore
    private Status status;

    // TODO @Ignore dimmerValue

    public LightModel() {}

    private LightModel(Builder builder) {
        this.uuid = builder.uuid;
        this.environmentId = builder.environmentId;
        this.gatewayUuid = builder.gatewayUuid;
        this.name = builder.name;
        this.where = builder.where;
        this.dimmer = builder.dimmer;
        this.favourite = builder.favourite;
    }

    public static class Builder {

        private final String uuid;
        private Integer environmentId;
        private String gatewayUuid;
        private String name;
        private Integer where;
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

        public Builder where(Integer where) {
            this.where = where;
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
            requireNonNull(environmentId, "environmentId is null");
            requireNonNull(gatewayUuid, "gatewayUuid is null");
            requireNonNull(name, "name is null");
            requireNonNull(where, "where is null");

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWhere() {
        return where;
    }

    public void setWhere(Integer where) {
        this.where = where;
    }

    public boolean isDimmer() {
        return dimmer;
    }

    public void setDimmer(boolean dimmer) {
        this.dimmer = dimmer;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
