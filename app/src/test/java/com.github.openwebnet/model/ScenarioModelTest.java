package com.github.openwebnet.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ScenarioModelTest {

    private final Integer SCENARIO_ENVIRONMENT = 100;
    private final String SCENARIO_GATEWAY = "gatewayUuid";
    private final String SCENARIO_NAME = "name";
    private final String SCENARIO_WHERE = "08";
    private final boolean SCENARIO_FAVOURITE = true;

    @Test(expected = NullPointerException.class)
    public void testScenarioModelBuilder_nullEnvironmentId() {
        ScenarioModel.addBuilder().environment(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testScenarioModelBuilder_nullGatewayUuid() {
        ScenarioModel.addBuilder().gateway(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testScenarioModelBuilder_nullName() {
        ScenarioModel.addBuilder().name(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testScenarioModelBuilder_nullWhere() {
        ScenarioModel.addBuilder().where(null).build();
    }

    @Test
    public void testScenarioModelAddBuilder_success() {
        ScenarioModel scenario = ScenarioModel.addBuilder()
            .environment(SCENARIO_ENVIRONMENT)
            .gateway(SCENARIO_GATEWAY)
            .name(SCENARIO_NAME)
            .where(SCENARIO_WHERE)
            .favourite(SCENARIO_FAVOURITE)
            .build();

        assertNotNull("invalid uuid", scenario.getUuid());
        assertCommonFields(scenario);
    }

    @Test
    public void testScenarioModelUpdateBuilder_success() {
        String LIGHT_UUID = "myUUid";
        ScenarioModel scenario = ScenarioModel.updateBuilder(LIGHT_UUID)
            .environment(SCENARIO_ENVIRONMENT)
            .gateway(SCENARIO_GATEWAY)
            .name(SCENARIO_NAME)
            .where(SCENARIO_WHERE)
            .favourite(SCENARIO_FAVOURITE)
            .build();

        assertEquals("invalid uuid", LIGHT_UUID, scenario.getUuid());
        assertCommonFields(scenario);
    }

    private void assertCommonFields(ScenarioModel scenario) {
        assertEquals("invalid environmentId", SCENARIO_ENVIRONMENT, scenario.getEnvironmentId());
        assertEquals("invalid gatewayUuid", SCENARIO_GATEWAY, scenario.getGatewayUuid());
        assertEquals("invalid name", SCENARIO_NAME, scenario.getName());
        assertEquals("invalid where", SCENARIO_WHERE, scenario.getWhere());
        assertEquals("invalid favourite", SCENARIO_FAVOURITE, scenario.isFavourite());
        assertNull("invalid status", scenario.getStatus());
    }

}
