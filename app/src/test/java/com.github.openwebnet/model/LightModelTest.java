package com.github.openwebnet.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class LightModelTest {

    private final Integer LIGHT_ENVIRONMENT = 100;
    private final String LIGHT_GATEWAY = "gatewayUuid";
    private final String LIGHT_NAME = "name";
    private final Integer LIGHT_WHERE = 8;
    private final boolean LIGHT_DIMMER = true;
    private final boolean LIGHT_FAVOURITE = true;

    @Test(expected = NullPointerException.class)
    public void testLightModelBuilder_nullEnvironmentId() {
        LightModel.addBuilder().environment(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testLightModelBuilder_nullGatewayUuid() {
        LightModel.addBuilder().gateway(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testLightModelBuilder_nullName() {
        LightModel.addBuilder().name(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testLightModelBuilder_nullWhere() {
        LightModel.addBuilder().where(null).build();
    }

    @Test
    public void testLightModelAddBuilder_success() {
        LightModel light = LightModel.addBuilder()
            .environment(LIGHT_ENVIRONMENT)
            .gateway(LIGHT_GATEWAY)
            .name(LIGHT_NAME)
            .where(LIGHT_WHERE)
            .dimmer(LIGHT_DIMMER)
            .favourite(LIGHT_FAVOURITE)
            .build();

        assertNotNull("invalid uuid", light.getUuid());
        assertCommonFields(light);
    }

    @Test
    public void testLightModelUpdateBuilder_success() {
        String LIGHT_UUID = "myUUid";
        LightModel light = LightModel.updateBuilder(LIGHT_UUID)
            .environment(LIGHT_ENVIRONMENT)
            .gateway(LIGHT_GATEWAY)
            .name(LIGHT_NAME)
            .where(LIGHT_WHERE)
            .dimmer(LIGHT_DIMMER)
            .favourite(LIGHT_FAVOURITE)
            .build();

        assertEquals("invalid uuid", LIGHT_UUID, light.getUuid());
        assertCommonFields(light);
    }

    private void assertCommonFields(LightModel light) {
        assertEquals("invalid environmentId", LIGHT_ENVIRONMENT, light.getEnvironmentId());
        assertEquals("invalid gatewayUuid", LIGHT_GATEWAY, light.getGatewayUuid());
        assertEquals("invalid name", LIGHT_NAME, light.getName());
        assertEquals("invalid where", LIGHT_WHERE, light.getWhere());
        assertEquals("invalid dimmer", LIGHT_DIMMER, light.isDimmer());
        assertEquals("invalid favourite", LIGHT_FAVOURITE, light.isFavourite());
        assertNull("invalid status", light.getStatus());
    }

}
