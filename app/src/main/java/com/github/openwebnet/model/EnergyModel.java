package com.github.openwebnet.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static com.google.common.base.Preconditions.checkNotNull;

public class EnergyModel extends RealmObject implements RealmModel, DomoticModel {

    public static final String FIELD_VERSION = "version";

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
    private String version;

    private boolean favourite;

    @Ignore
    private String instantaneousPower;

    @Ignore
    private String dailyPower;

    @Ignore
    private String monthlyPower;

    public EnergyModel() {}

    private EnergyModel(Builder builder) {
        this.uuid = builder.uuid;
        this.environmentId = builder.environmentId;
        this.gatewayUuid = builder.gatewayUuid;
        this.name = builder.name;
        this.where = builder.where;
        this.version = builder.version;
        this.favourite = builder.favourite;
    }

    public static class Builder {

        private final String uuid;
        private Integer environmentId;
        private String gatewayUuid;
        private String name;
        private String where;
        private String version;
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

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder favourite(boolean favourite) {
            this.favourite = favourite;
            return this;
        }

        public EnergyModel build() {
            checkNotNull(environmentId, "environmentId is null");
            checkNotNull(gatewayUuid, "gatewayUuid is null");
            checkNotNull(name, "name is null");
            checkNotNull(where, "where is null");
            checkNotNull(version, "version is null");

            return new EnergyModel(this);
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean isFavourite() {
        return favourite;
    }

    @Override
    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getInstantaneousPower() {
        return instantaneousPower;
    }

    public void setInstantaneousPower(String instantaneousPower) {
        this.instantaneousPower = instantaneousPower;
    }

    public String getDailyPower() {
        return dailyPower;
    }

    public void setDailyPower(String dailyPower) {
        this.dailyPower = dailyPower;
    }

    public String getMonthlyPower() {
        return monthlyPower;
    }

    public void setMonthlyPower(String monthlyPower) {
        this.monthlyPower = monthlyPower;
    }

}
