package com.github.openwebnet.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DeviceModelTest {

    private final Integer DEVICE_ENVIRONMENT = 101;
    private final String DEVICE_GATEWAY = "gatewayUuid";
    private final String DEVICE_NAME = "name";
    private final String DEVICE_REQUEST = "request";
    private final String DEVICE_RESPONSE = "response";
    private final boolean DEVICE_FAVOURITE = true;
    private final boolean DEVICE_RUN_ON_LOAD = true;
    private final boolean DEVICE_CONFIRMATION = true;

    @Test(expected = NullPointerException.class)
    public void testDeviceModelBuilder_nullEnvironmentId() {
        DeviceModel.addBuilder().environment(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testDeviceModelBuilder_nullGatewayUuid() {
        DeviceModel.addBuilder().gateway(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testDeviceModelBuilder_nullName() {
        DeviceModel.addBuilder().name(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testDeviceModelBuilder_nullRequest() {
        DeviceModel.addBuilder().request(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testDeviceModelBuilder_nullResponse() {
        DeviceModel.addBuilder().response(null).build();
    }

    @Test
    public void testDeviceModelAddBuilder_success() {
        DeviceModel device = DeviceModel.addBuilder()
            .environment(DEVICE_ENVIRONMENT)
            .gateway(DEVICE_GATEWAY)
            .name(DEVICE_NAME)
            .request(DEVICE_REQUEST)
            .response(DEVICE_RESPONSE)
            .favourite(DEVICE_FAVOURITE)
            .runOnLoad(DEVICE_RUN_ON_LOAD)
            .showConfirmation(DEVICE_CONFIRMATION)
            .build();

        assertNotNull("invalid uuid", device.getUuid());
        assertCommonFields(device);
    }

    private void assertCommonFields(DeviceModel device) {
        assertEquals("invalid environmentId", DEVICE_ENVIRONMENT, device.getEnvironmentId());
        assertEquals("invalid gatewayUuid", DEVICE_GATEWAY, device.getGatewayUuid());
        assertEquals("invalid name", DEVICE_NAME, device.getName());
        assertEquals("invalid request", DEVICE_REQUEST, device.getRequest());
        assertEquals("invalid response", DEVICE_RESPONSE, device.getResponse());
        assertEquals("invalid favourite", DEVICE_FAVOURITE, device.isFavourite());
        assertEquals("invalid runOnLoad", DEVICE_RUN_ON_LOAD, device.isFavourite());
        assertEquals("invalid showConfirmation", DEVICE_CONFIRMATION, device.isShowConfirmation());
        assertNull("invalid status", device.getStatus());
    }

    @Test
    public void testDeviceModelUpdateBuilder_success() {
        String DEVICE_UUID = "myUUid";

        DeviceModel device = DeviceModel.updateBuilder(DEVICE_UUID)
            .environment(DEVICE_ENVIRONMENT)
            .gateway(DEVICE_GATEWAY)
            .name(DEVICE_NAME)
            .request(DEVICE_REQUEST)
            .response(DEVICE_RESPONSE)
            .favourite(DEVICE_FAVOURITE)
            .runOnLoad(DEVICE_RUN_ON_LOAD)
            .showConfirmation(DEVICE_CONFIRMATION)
            .build();

        assertEquals("invalid uuid", DEVICE_UUID, device.getUuid());
        assertCommonFields(device);
    }

}
