package com.github.openwebnet.model;

import com.github.niqdev.openwebnet.message.EnergyManagement;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EnergyModelTest {

    private final Integer ENERGY_ENVIRONMENT = 100;
    private final String ENERGY_GATEWAY = "gatewayUuid";
    private final String ENERGY_NAME = "name";
    private final String ENERGY_WHERE = "08";
    private final EnergyManagement.Version ENERGY_VERSION = EnergyManagement.Version.MODEL_F523;
    private final boolean ENERGY_FAVOURITE = true;

    @Test(expected = NullPointerException.class)
    public void testEnergyModelBuilder_nullEnvironmentId() {
        EnergyModel.addBuilder().environment(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testEnergyModelBuilder_nullGatewayUuid() {
        EnergyModel.addBuilder().gateway(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testEnergyModelBuilder_nullName() {
        EnergyModel.addBuilder().name(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testEnergyModelBuilder_nullWhere() {
        EnergyModel.addBuilder().where(null).build();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testEnergyModel_getVersion() {
        EnergyModel energy = new EnergyModel();
        energy.getVersion();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testEnergyModel_setVersion() {
        EnergyModel energy = new EnergyModel();
        energy.setVersion("VERSION");
    }

    @Test
    public void testEnergyModelAddBuilder_success() {
        EnergyModel energy = EnergyModel.addBuilder()
            .environment(ENERGY_ENVIRONMENT)
            .gateway(ENERGY_GATEWAY)
            .name(ENERGY_NAME)
            .where(ENERGY_WHERE)
            .version(ENERGY_VERSION)
            .favourite(ENERGY_FAVOURITE)
            .build();

        assertNotNull("invalid uuid", energy.getUuid());
        assertCommonFields(energy);
    }

    @Test
    public void testEnergyModelUpdateBuilder_success() {
        String ENERGY_UUID = "myUUid";
        EnergyModel energy = EnergyModel.updateBuilder(ENERGY_UUID)
            .environment(ENERGY_ENVIRONMENT)
            .gateway(ENERGY_GATEWAY)
            .name(ENERGY_NAME)
            .where(ENERGY_WHERE)
            .version(ENERGY_VERSION)
            .favourite(ENERGY_FAVOURITE)
            .build();

        assertEquals("invalid uuid", ENERGY_UUID, energy.getUuid());
        assertCommonFields(energy);
    }

    private void assertCommonFields(EnergyModel energy) {
        assertEquals("invalid environmentId", ENERGY_ENVIRONMENT, energy.getEnvironmentId());
        assertEquals("invalid gatewayUuid", ENERGY_GATEWAY, energy.getGatewayUuid());
        assertEquals("invalid name", ENERGY_NAME, energy.getName());
        assertEquals("invalid where", ENERGY_WHERE, energy.getWhere());
        assertEquals("invalid version", ENERGY_VERSION, energy.getEnergyManagementVersion());
        assertEquals("invalid favourite", ENERGY_FAVOURITE, energy.isFavourite());
    }
    
}
