package com.github.openwebnet.model;

/**
 * @see DeviceModel
 * @see LightModel
 */
public interface DomoticModel {

    String FIELD_ENVIRONMENT_ID = "environmentId";
    String FIELD_FAVOURITE = "favourite";

    Integer getEnvironmentId();

    boolean isFavourite();

}
