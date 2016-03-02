package com.github.openwebnet.model;

/**
 * @see DeviceModel
 * @see LightModel
 * @see AutomationModel
 */
public interface DomoticModel {

    String FIELD_ENVIRONMENT_ID = "environmentId";
    String FIELD_GATEWAY_UUID = "gatewayUuid";
    String FIELD_FAVOURITE = "favourite";
    String FIELD_NAME = "name";

    Integer getEnvironmentId();

    String getGatewayUuid();

    boolean isFavourite();

    String getName();

}
