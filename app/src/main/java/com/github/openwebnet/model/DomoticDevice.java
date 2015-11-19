package com.github.openwebnet.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static com.google.common.base.Preconditions.checkNotNull;

public class DomoticDevice extends RealmObject {

    @PrimaryKey
    private String uuid;

    @Required
    private Integer environmentId;

    @Required
    private String name;

    @Required
    private String rawValue;

    private String description;

    public DomoticDevice() {}

    private DomoticDevice(Builder builder) {
        this.uuid = builder.uuid;
        this.name = builder.name;
        this.description = builder.description;
    }

    /**
     * Prefer this builder to instantiate a new {@link DomoticDevice}.
     *
     * Note:
     * {@link lombok.Getter} and {@link lombok.Setter} don't work with {@link RealmObject}
     */
    public static class Builder {

        private final String uuid;
        private Integer environmentId;
        private String name;
        private String rawValue;
        private String description;

        public Builder() {
            this.uuid = UUID.randomUUID().toString();
        }

        public Builder environmentId(Integer environmentId) {
            this.environmentId = environmentId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder rawValue(String value) {
            this.rawValue = value;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public DomoticDevice build() {
            checkNotNull(environmentId, "invalid DomoticDevice:environmentId");
            checkNotNull(name, "invalid DomoticDevice:name");
            checkNotNull(rawValue, "invalid DomoticDevice:rawValue");
            return new DomoticDevice(this);
        }
    }

    public static Builder newBuilder() {
        return new DomoticDevice.Builder();
    }

    // getter/setter

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRawValue() {
        return rawValue;
    }

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
