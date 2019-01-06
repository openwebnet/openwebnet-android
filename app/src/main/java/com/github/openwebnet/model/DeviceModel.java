package com.github.openwebnet.model;

import com.github.openwebnet.model.firestore.FirestoreModel;
import com.github.openwebnet.model.firestore.ProfileVersionModel;

import org.threeten.bp.Instant;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static com.google.common.base.Preconditions.checkNotNull;

public class DeviceModel extends RealmObject
        implements RealmModel, DomoticModel, FirestoreModel<DeviceModel> {

    public static final String FIELD_REQUEST = "request";
    public static final String FIELD_RESPONSE = "response";
    public static final String FIELD_RUN_ON_LOAD = "runOnLoad";
    public static final String FIELD_SHOW_CONFIRMATION = "showConfirmation";

    public enum Status {
        SUCCESS, FAIL
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
    private String request;

    @Required
    private String response;

    private boolean favourite;

    private boolean runOnLoad;

    private boolean showConfirmation;

    @Ignore
    private Status status;

    @Ignore
    private String responseDebug;

    @Ignore
    private Instant instantRequestDebug;

    @Ignore
    private Instant instantResponseDebug;

    public DeviceModel() {}

    private DeviceModel(Builder builder) {
        this.uuid = builder.uuid;
        this.environmentId = builder.environmentId;
        this.gatewayUuid = builder.gatewayUuid;
        this.name = builder.name;
        this.request = builder.request;
        this.response = builder.response;
        this.favourite = builder.favourite;
        this.runOnLoad = builder.runOnLoad;
        this.showConfirmation = builder.showConfirmation;
    }

    public static class Builder {

        private final String uuid;
        private Integer environmentId;
        private String gatewayUuid;
        private String name;
        private String request;
        private String response;
        private boolean favourite;
        private boolean runOnLoad;
        private boolean showConfirmation;

        private Builder(String uuid) {
            this.uuid = uuid;
        }

        private Builder(Map<String, Object> map) {
            this.uuid = (String) map.get(FIELD_UUID);
            this.environmentId = ((Long) map.get(FIELD_ENVIRONMENT_ID)).intValue();
            this.gatewayUuid = (String) map.get(FIELD_GATEWAY_UUID);
            this.name = (String) map.get(FIELD_NAME);
            this.request = (String) map.get(FIELD_REQUEST);
            this.response = (String) map.get(FIELD_RESPONSE);
            this.favourite = (Boolean) map.get(FIELD_FAVOURITE);
            this.runOnLoad = (Boolean) map.get(FIELD_RUN_ON_LOAD);
            this.showConfirmation = (Boolean) map.get(FIELD_SHOW_CONFIRMATION);
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

        public Builder request(String request) {
            this.request = request;
            return this;
        }

        public Builder response(String response) {
            this.response = response;
            return this;
        }

        public Builder favourite(boolean favourite) {
            this.favourite = favourite;
            return this;
        }

        public Builder runOnLoad(boolean value) {
            this.runOnLoad = value;
            return this;
        }

        public Builder showConfirmation(boolean value) {
            this.showConfirmation = value;
            return this;
        }

        public DeviceModel build() {
            checkNotNull(environmentId, "environmentId is null");
            checkNotNull(gatewayUuid, "gatewayUuid is null");
            checkNotNull(name, "name is null");
            checkNotNull(request, "request is null");
            checkNotNull(response, "response is null");

            return new DeviceModel(this);
        }
    }

    public static Builder addBuilder() {
        return new Builder(UUID.randomUUID().toString());
    }

    public static Builder updateBuilder(String uuid) {
        return new Builder(uuid);
    }

    public static DeviceModel newInstance(Map<String, Object> map, ProfileVersionModel version) {
        return new DeviceModel().fromMap(map, version);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_UUID, getUuid());
        map.put(FIELD_ENVIRONMENT_ID, getEnvironmentId());
        map.put(FIELD_GATEWAY_UUID, getGatewayUuid());
        map.put(FIELD_NAME, getName());
        map.put(FIELD_REQUEST, getRequest());
        map.put(FIELD_RESPONSE, getResponse());
        map.put(FIELD_FAVOURITE, isFavourite());
        map.put(FIELD_RUN_ON_LOAD, isRunOnLoad());
        map.put(FIELD_SHOW_CONFIRMATION, isShowConfirmation());
        return map;
    }

    @Override
    public DeviceModel fromMap(Map<String, Object> map, ProfileVersionModel version) {
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

    @Override
    public boolean isFavourite() {
        return favourite;
    }

    @Override
    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean isRunOnLoad() {
        return runOnLoad;
    }

    public void setRunOnLoad(boolean runOnLoad) {
        this.runOnLoad = runOnLoad;
    }

    public boolean isShowConfirmation() {
        return showConfirmation;
    }

    public void setShowConfirmation(boolean showConfirmation) {
        this.showConfirmation = showConfirmation;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getResponseDebug() {
        return responseDebug;
    }

    public void setResponseDebug(String responseDebug) {
        this.responseDebug = responseDebug;
    }

    public Instant getInstantRequestDebug() {
        return instantRequestDebug;
    }

    public void setInstantRequestDebug(Instant instantRequestDebug) {
        this.instantRequestDebug = instantRequestDebug;
    }

    public Instant getInstantResponseDebug() {
        return instantResponseDebug;
    }

    public void setInstantResponseDebug(Instant instantResponseDebug) {
        this.instantResponseDebug = instantResponseDebug;
    }

}
