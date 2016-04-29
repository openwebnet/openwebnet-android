package com.github.openwebnet.model;

/*
 * @see DeviceModel
 * @see LightModel
 * @see AutomationModel
 * @see IpcamModel
 *
 * Note:
 * check https://github.com/openwebnet/openwebnet-android/pull/29
 * For now if you add a new class that implements this interface,
 * you must remember to verify EnvironmentRepositoryImpl#delete
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
