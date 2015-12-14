package com.github.openwebnet.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static java.util.Objects.requireNonNull;

public class LightModel extends RealmObject implements RealmModel {

    @PrimaryKey
    private String uuid;

    private EnvironmentModel environment;

    private GatewayModel gateway;

    @Required
    private String name;

    @Required
    private Integer where;

    private boolean dimmer;

    private boolean favourite;

    public LightModel() {}

    private LightModel(Builder builder) {
        this.uuid = builder.uuid;
        this.environment = builder.environment;
        this.gateway = builder.gateway;
        this.name = builder.name;
        this.where = builder.where;
        this.dimmer = builder.dimmer;
        this.favourite = builder.favourite;
    }

    public static class Builder {

        private final String uuid;
        private EnvironmentModel environment;
        private GatewayModel gateway;
        private String name;
        private Integer where;
        private boolean dimmer;
        private boolean favourite;

        public Builder() {
            this.uuid = UUID.randomUUID().toString();
        }

        public Builder environment(EnvironmentModel environment) {
            this.environment = environment;
            return this;
        }

        public Builder gateway(GatewayModel gateway) {
            this.gateway = gateway;
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
            requireNonNull(environment, "environment is null");
            requireNonNull(gateway, "gateway is null");
            requireNonNull(name, "name is null");
            requireNonNull(where, "where is null");

            return new LightModel(this);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public EnvironmentModel getEnvironment() {
        return environment;
    }

    public void setEnvironment(EnvironmentModel environment) {
        this.environment = environment;
    }

    public GatewayModel getGateway() {
        return gateway;
    }

    public void setGateway(GatewayModel gateway) {
        this.gateway = gateway;
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
}
