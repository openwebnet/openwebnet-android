package com.github.openwebnet.model;

import com.github.niqdev.openwebnet.message.Automation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AutomationModelTest {

    private final Integer AUTOMATION_ENVIRONMENT = 100;
    private final String AUTOMATION_GATEWAY = "gatewayUuid";
    private final String AUTOMATION_NAME = "name";
    private final String AUTOMATION_WHERE = "08";
    private final Automation.Type AUTOMATION_TYPE = Automation.Type.POINT_TO_POINT;
    private final String AUTOMATION_BUS = Automation.NO_BUS;
    private final boolean AUTOMATION_FAVOURITE = true;

    @Test(expected = NullPointerException.class)
    public void testAutomationModelBuilder_nullEnvironmentId() {
        AutomationModel.addBuilder().environment(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testAutomationModelBuilder_nullGatewayUuid() {
        AutomationModel.addBuilder().gateway(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testAutomationModelBuilder_nullName() {
        AutomationModel.addBuilder().name(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testAutomationModelBuilder_nullWhere() {
        AutomationModel.addBuilder().where(null).build();
    }

    @Test
    public void testAutomationModelAddBuilder_success() {
        AutomationModel automation = AutomationModel.addBuilder()
            .environment(AUTOMATION_ENVIRONMENT)
            .gateway(AUTOMATION_GATEWAY)
            .name(AUTOMATION_NAME)
            .where(AUTOMATION_WHERE)
            .type(AUTOMATION_TYPE)
            .bus(AUTOMATION_BUS)
            .favourite(AUTOMATION_FAVOURITE)
            .build();

        assertNotNull("invalid uuid", automation.getUuid());
        assertCommonFields(automation);
    }

    @Test
    public void testAutomationModelUpdateBuilder_success() {
        String LIGHT_UUID = "myUUid";
        AutomationModel automation = AutomationModel.updateBuilder(LIGHT_UUID)
            .environment(AUTOMATION_ENVIRONMENT)
            .gateway(AUTOMATION_GATEWAY)
            .name(AUTOMATION_NAME)
            .where(AUTOMATION_WHERE)
            .type(AUTOMATION_TYPE)
            .bus(AUTOMATION_BUS)
            .favourite(AUTOMATION_FAVOURITE)
            .build();

        assertEquals("invalid uuid", LIGHT_UUID, automation.getUuid());
        assertCommonFields(automation);
    }

    private void assertCommonFields(AutomationModel automation) {
        assertEquals("invalid environmentId", AUTOMATION_ENVIRONMENT, automation.getEnvironmentId());
        assertEquals("invalid gatewayUuid", AUTOMATION_GATEWAY, automation.getGatewayUuid());
        assertEquals("invalid name", AUTOMATION_NAME, automation.getName());
        assertEquals("invalid where", AUTOMATION_WHERE, automation.getWhere());
        assertEquals("invalid favourite", AUTOMATION_FAVOURITE, automation.isFavourite());
        assertNull("invalid status", automation.getStatus());
    }

}
