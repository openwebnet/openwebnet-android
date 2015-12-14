package com.github.openwebnet.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static java.util.Objects.requireNonNull;

public class DeviceModel extends RealmModel {

    @PrimaryKey
    private String uuid;

    private EnvironmentModel environment;

    private GatewayModel gateway;

    @Required
    private String name;

    @Required
    private String request;

    private String response;

    private String positiveMessage;

    private String negativeMessage;

    private boolean runOnLoad;

    private boolean favourite;

    public DeviceModel() {
    }

    public DeviceModel(Builder builder) {
        this.uuid = builder.uuid;
        this.environment = builder.environment;
        this.gateway = builder.gateway;
        this.name = builder.name;
        this.request = builder.request;
        this.response = builder.response;
        this.positiveMessage = builder.positiveMessage;
        this.negativeMessage = builder.negativeMessage;
        this.runOnLoad = builder.runOnLoad;
        this.favourite = builder.favourite;
    }

    /**
     * Prefer this builder to instantiate a new {@link DeviceModel}.
     * <p>
     * Note:
     * {@link lombok.Getter} and {@link lombok.Setter} don't work with {@link RealmObject}.
     * Avoid to use setXXX, {@link DeviceModel} should be immutable.
     */
    public static class Builder {

        private final String uuid;
        private EnvironmentModel environment;
        private GatewayModel gateway;
        private String name;
        private String request;
        private String response;
        private String positiveMessage;
        private String negativeMessage;
        private boolean runOnLoad;
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

        public Builder request(String request) {
            this.request = request;
            return this;
        }

        public Builder response(String response) {
            this.response = response;
            return this;
        }

        public Builder positiveMessage(String message) {
            this.positiveMessage = message;
            return this;
        }

        public Builder negativeMessage(String message) {
            this.positiveMessage = message;
            return this;
        }

        public Builder runOnLoad(boolean value) {
            this.runOnLoad = value;
            return this;
        }

        public Builder favourite(boolean favourite) {
            this.favourite = favourite;
            return this;
        }

        public DeviceModel build() {
            requireNonNull(environment, "environment is null");
            requireNonNull(gateway, "gateway is null");
            requireNonNull(name, "name is null");
            requireNonNull(request, "request is null");

            return new DeviceModel(this);
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

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getPositiveMessage() {
        return positiveMessage;
    }

    public void setPositiveMessage(String positiveMessage) {
        this.positiveMessage = positiveMessage;
    }

    public String getNegativeMessage() {
        return negativeMessage;
    }

    public void setNegativeMessage(String negativeMessage) {
        this.negativeMessage = negativeMessage;
    }

    public boolean isRunOnLoad() {
        return runOnLoad;
    }

    public void setRunOnLoad(boolean runOnLoad) {
        this.runOnLoad = runOnLoad;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
