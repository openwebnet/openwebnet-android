package com.github.openwebnet.model;

/**
 * @see DeviceModel
 * @see LightModel
 */
public interface DomoticModel {

    String FIELD_ENVIRONMENT_ID = "environmentId";
    String FIELD_FAVOURITE = "favourite";
    String FIELD_NAME = "name";

    Integer getEnvironmentId();

    boolean isFavourite();

    String getName();

}
