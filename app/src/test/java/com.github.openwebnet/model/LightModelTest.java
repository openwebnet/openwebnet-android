package com.github.openwebnet.model;

import com.github.niqdev.openwebnet.message.Lighting;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class LightModelTest {

    private final Integer LIGHT_ENVIRONMENT = 100;
    private final String LIGHT_GATEWAY = "gatewayUuid";
    private final String LIGHT_NAME = "name";
    private final String LIGHT_WHERE = "08";
    private final Lighting.Type LIGHT_TYPE = Lighting.Type.POINT_TO_POINT;
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

    @Test(expected = UnsupportedOperationException.class)
    public void testLightModel_getType() {
        LightModel light = new LightModel();
        light.getType();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLightModel_setType() {
        LightModel light = new LightModel();
        light.setType("TYPE");
    }

    @Test
    public void testLightModelAddBuilder_success() {
        LightModel light = LightModel.addBuilder()
            .environment(LIGHT_ENVIRONMENT)
            .gateway(LIGHT_GATEWAY)
            .name(LIGHT_NAME)
            .where(LIGHT_WHERE)
            .type(LIGHT_TYPE)
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
            .type(LIGHT_TYPE)
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
        assertEquals("invalid type", LIGHT_TYPE, light.getLightingType());
        assertEquals("invalid favourite", LIGHT_FAVOURITE, light.isFavourite());
        assertNull("invalid status", light.getStatus());
    }

}
