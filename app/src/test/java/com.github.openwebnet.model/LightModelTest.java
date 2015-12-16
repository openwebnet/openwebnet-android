package com.github.openwebnet.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LightModelTest {

    @Test(expected = NullPointerException.class)
    public void testLightModelBuilder_nullEnvironmentId() {
        LightModel.newBuilder().environment(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testLightModelBuilder_nullGatewayUuid() {
        LightModel.newBuilder().gateway(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testLightModelBuilder_nullName() {
        LightModel.newBuilder().name(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testLightModelBuilder_nullWhere() {
        LightModel.newBuilder().where(null).build();
    }

    @Test
    public void testLightModelBuilder_success() {
        String LIGHT_NAME = "name";
        Integer LIGHT_WHERE = 8;
        boolean LIGHT_DIMMER = true;
        Integer LIGHT_ENVIRONMENT = 100;
        String LIGHT_GATEWAY = "gateway";
        boolean LIGHT_FAVOURITE = true;

        LightModel light = LightModel.newBuilder()
            .name(LIGHT_NAME)
            .where(LIGHT_WHERE)
            .dimmer(LIGHT_DIMMER)
            .environment(LIGHT_ENVIRONMENT)
            .gateway(LIGHT_GATEWAY)
            .favourite(LIGHT_FAVOURITE).build();

        assertEquals("invalid name", LIGHT_NAME, light.getName());
        assertEquals("invalid where", LIGHT_WHERE, light.getWhere());
        assertEquals("invalid dimmer", LIGHT_DIMMER, light.isDimmer());
        assertEquals("invalid environmentId", LIGHT_ENVIRONMENT, light.getEnvironmentId());
        assertEquals("invalid gatewayUuid", LIGHT_GATEWAY, light.getGatewayUuid());
        assertEquals("invalid favourite", LIGHT_FAVOURITE, light.isFavourite());
    }

}
