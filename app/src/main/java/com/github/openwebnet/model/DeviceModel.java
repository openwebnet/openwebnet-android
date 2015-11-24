package com.github.openwebnet.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static com.google.common.base.Preconditions.checkNotNull;

// TODO use RealmObject for relationship i.e. EnvironmentModel and GatewayModel
public class DeviceModel extends RealmObject {

    @PrimaryKey
    private String uuid;

    @Required
    private Integer environment;

    @Required
    private String gateway;

    @Required
    private String name;

    @Required
    private String raw;

    private String description;

    public DeviceModel(){}

    public DeviceModel(Builder builder) {
        this.uuid = builder.uuid;
        this.environment = builder.environment;
        this.gateway = builder.gateway;
        this.name = builder.name;
        this.raw = builder.raw;
        this.description = builder.description;
    }

    /**
     * Prefer this builder to instantiate a new {@link DeviceModel}.
     *
     * Note:
     * {@link lombok.Getter} and {@link lombok.Setter} don't work with {@link RealmObject}.
     * Avoid to use setXXX, {@link DeviceModel} should be immutable.
     */
    public static class Builder {

        private final String uuid;
        private Integer environment;
        private String gateway;
        private String name;
        private String raw;
        private String description;

        public Builder() {
            this.uuid = UUID.randomUUID().toString();
        }

        public Builder environment(Integer id) {
            this.environment = id;
            return this;
        }

        public Builder gateway(String uuid) {
            this.gateway = uuid;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder raw(String value) {
            this.raw = value;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public DeviceModel build() {
            checkNotNull(environment, "invalid DeviceModel:environment");
            checkNotNull(gateway, "invalid DeviceModel:gateway");
            checkNotNull(name, "invalid DeviceModel:name");
            checkNotNull(raw, "invalid DeviceModel:raw");
            return new DeviceModel(this);
        }
    }

    public static Builder newBuilder() {
        return new DeviceModel.Builder();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getEnvironment() {
        return environment;
    }

    public void setEnvironment(Integer environment) {
        this.environment = environment;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
