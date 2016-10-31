package com.github.openwebnet.model;

import com.github.niqdev.openwebnet.message.SoundSystem;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static com.google.common.base.Preconditions.checkNotNull;

public class SoundModel extends RealmObject implements RealmModel, DomoticModel {

    public static final String FIELD_SOURCE = "source";
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
    private String source;

    @Required
    private String type;

    private boolean favourite;

    @Ignore
    private Status status;

    @Ignore
    private SoundSystem.Source soundSystemSource;

    @Ignore
    private SoundSystem.Type soundSystemType;

    public SoundModel() {}

    private SoundModel(Builder builder) {
        this.uuid = builder.uuid;
        this.environmentId = builder.environmentId;
        this.gatewayUuid = builder.gatewayUuid;
        this.name = builder.name;
        this.where = builder.where;
        this.type = builder.type;
        this.source = builder.source;
        this.favourite = builder.favourite;
    }

    public static class Builder {

        private final String uuid;
        private Integer environmentId;
        private String gatewayUuid;
        private String name;
        private String where;
        private String source;
        private String type;
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

        public Builder source(SoundSystem.Source source) {
            this.source = source.name();
            return this;
        }

        public Builder type(SoundSystem.Type type) {
            this.type = type.name();
            return this;
        }

        public Builder favourite(boolean favourite) {
            this.favourite = favourite;
            return this;
        }

        public SoundModel build() {
            checkNotNull(environmentId, "environmentId is null");
            checkNotNull(gatewayUuid, "gatewayUuid is null");
            checkNotNull(name, "name is null");
            checkNotNull(where, "where is null");
            checkNotNull(source, "type is source");
            checkNotNull(type, "type is null");

            return new SoundModel(this);
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

    public String getSource() {
        throw new UnsupportedOperationException("method not implemented: use SoundModel#getSoundSystemSource");
    }

    public void setSource(String source) {
        throw new UnsupportedOperationException("method not implemented: use SoundModel#setSoundSystemSource");
    }

    public String getType() {
        throw new UnsupportedOperationException("method not implemented: use SoundModel#getSoundSystemType");
    }

    public void setType(String type) {
        throw new UnsupportedOperationException("method not implemented: use SoundModel#setSoundSystemType");
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

    public SoundSystem.Source getSoundSystemSource() {
        return soundSystemSource;
    }

    public void setSoundSystemSource(SoundSystem.Source soundSystemSource) {
        this.soundSystemSource = soundSystemSource;
    }

    public SoundSystem.Type getSoundSystemType() {
        return soundSystemType;
    }

    public void setSoundSystemType(SoundSystem.Type soundSystemType) {
        this.soundSystemType = soundSystemType;
    }
}
