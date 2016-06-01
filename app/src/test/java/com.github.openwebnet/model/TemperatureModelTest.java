package com.github.openwebnet.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TemperatureModelTest {

    private final Integer TEMPERATURE_ENVIRONMENT = 100;
    private final String TEMPERATURE_GATEWAY = "gatewayUuid";
    private final String TEMPERATURE_NAME = "name";
    private final String TEMPERATURE_WHERE = "08";
    private final boolean TEMPERATURE_FAVOURITE = true;

    @Test(expected = NullPointerException.class)
    public void testTemperatureModelBuilder_nullEnvironmentId() {
        TemperatureModel.addBuilder().environment(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testTemperatureModelBuilder_nullGatewayUuid() {
        TemperatureModel.addBuilder().gateway(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testTemperatureModelBuilder_nullName() {
        TemperatureModel.addBuilder().name(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testTemperatureModelBuilder_nullWhere() {
        TemperatureModel.addBuilder().where(null).build();
    }

    @Test
    public void testTemperatureModelAddBuilder_success() {
        TemperatureModel temperature = TemperatureModel.addBuilder()
            .environment(TEMPERATURE_ENVIRONMENT)
            .gateway(TEMPERATURE_GATEWAY)
            .name(TEMPERATURE_NAME)
            .where(TEMPERATURE_WHERE)
            .favourite(TEMPERATURE_FAVOURITE)
            .build();

        assertNotNull("invalid uuid", temperature.getUuid());
        assertCommonFields(temperature);
    }

    @Test
    public void testTemperatureModelUpdateBuilder_success() {
        String TEMPERATURE_UUID = "myUUid";
        TemperatureModel temperature = TemperatureModel.updateBuilder(TEMPERATURE_UUID)
            .environment(TEMPERATURE_ENVIRONMENT)
            .gateway(TEMPERATURE_GATEWAY)
            .name(TEMPERATURE_NAME)
            .where(TEMPERATURE_WHERE)
            .favourite(TEMPERATURE_FAVOURITE)
            .build();

        assertEquals("invalid uuid", TEMPERATURE_UUID, temperature.getUuid());
        assertCommonFields(temperature);
    }

    private void assertCommonFields(TemperatureModel temperature) {
        assertEquals("invalid environmentId", TEMPERATURE_ENVIRONMENT, temperature.getEnvironmentId());
        assertEquals("invalid gatewayUuid", TEMPERATURE_GATEWAY, temperature.getGatewayUuid());
        assertEquals("invalid name", TEMPERATURE_NAME, temperature.getName());
        assertEquals("invalid where", TEMPERATURE_WHERE, temperature.getWhere());
        assertEquals("invalid favourite", TEMPERATURE_FAVOURITE, temperature.isFavourite());
    }
    
}
