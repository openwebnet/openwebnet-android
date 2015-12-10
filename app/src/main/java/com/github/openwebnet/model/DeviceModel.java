package com.github.openwebnet.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static java.util.Objects.requireNonNull;

public class DeviceModel extends RealmObject {

    public enum Type {
        LIGHTING,
        //TEMPERATURE_CONTROL,
        //SOUND_SYSTEM,
        GENERIC;
    }

    @PrimaryKey
    private String uuid;

    //@Required
    private EnvironmentModel environment;

    //@Required
    @Ignore
    private Type type;

    //@Required
    private GatewayModel gateway;

    @Required
    private String name;

    private String description;

    @Required
    private String command;

    @Required
    private Boolean verifyStatusOnLoad;

    private String status;

    private String messagePositiveStatus;

    private String messageNegativeStatus;

    public DeviceModel(){}

    public DeviceModel(Builder builder) {
        this.uuid = builder.uuid;
        this.environment = builder.environment;
        this.type = builder.type;
        this.gateway = builder.gateway;
        this.name = builder.name;
        this.description = builder.description;
        this.command = builder.command;
        this.verifyStatusOnLoad = builder.verifyStatusOnLoad;
        this.status = builder.status;
        this.messagePositiveStatus = builder.messagePositiveStatus;
        this.messageNegativeStatus = builder.messageNegativeStatus;
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
        private EnvironmentModel environment;
        private Type type;
        private GatewayModel gateway;
        private String name;
        private String description;
        private String command;
        private Boolean verifyStatusOnLoad;
        private String status;
        private String messagePositiveStatus;
        private String messageNegativeStatus;

        public Builder() {
            this.uuid = UUID.randomUUID().toString();
        }

        public Builder environment(EnvironmentModel environment) {
            this.environment = environment;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
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

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder command(String command) {
            this.command = command;
            return this;
        }

        public Builder verifyStatusOnLoad(Boolean value) {
            this.verifyStatusOnLoad = value;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder messagePositiveStatus(String message) {
            this.messagePositiveStatus = message;
            return this;
        }

        public Builder messageNegativeStatus(String message) {
            this.messageNegativeStatus = message;
            return this;
        }

        public DeviceModel build() {
            requireNonNull(environment, "environment is null");
            requireNonNull(type, "type is null");
            requireNonNull(gateway, "gateway is null");
            requireNonNull(name, "name is null");
            requireNonNull(command, "command is null");
            requireNonNull(verifyStatusOnLoad, "verifyStatusOnLoad is null");

            if (verifyStatusOnLoad) {
                requireNonNull(status, "status is null");
                requireNonNull(messagePositiveStatus, "messagePositiveStatus is null");
                requireNonNull(messageNegativeStatus, "messageNegativeStatus is null");
            }

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

    public EnvironmentModel getEnvironment() {
        return environment;
    }

    public void setEnvironment(EnvironmentModel environment) {
        this.environment = environment;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Boolean getVerifyStatusOnLoad() {
        return verifyStatusOnLoad;
    }

    public void setVerifyStatusOnLoad(Boolean verifyStatusOnLoad) {
        this.verifyStatusOnLoad = verifyStatusOnLoad;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessagePositiveStatus() {
        return messagePositiveStatus;
    }

    public void setMessagePositiveStatus(String messagePositiveStatus) {
        this.messagePositiveStatus = messagePositiveStatus;
    }

    public String getMessageNegativeStatus() {
        return messageNegativeStatus;
    }

    public void setMessageNegativeStatus(String messageNegativeStatus) {
        this.messageNegativeStatus = messageNegativeStatus;
    }
}
