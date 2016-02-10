package com.github.openwebnet.matcher;

import com.github.openwebnet.model.DeviceModel;
import com.google.common.base.Preconditions;

import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

public class DeviceModelMatcher extends ArgumentMatcher<DeviceModel> {

    private final DeviceModel expected;

    private DeviceModelMatcher(DeviceModel expected) {
        Preconditions.checkNotNull(expected);
        this.expected = expected;
    }

    @Override
    public boolean matches(Object argument) {
        if (argument == null || !(argument instanceof DeviceModel)) {
            return false;
        }
        // ignore uuid
        DeviceModel actual = (DeviceModel) argument;
        return actual.getName().equals(expected.getName()) &&
            actual.getRequest().equals(expected.getRequest()) &&
            actual.getResponse().equals(expected.getResponse()) &&
            actual.getEnvironmentId().equals(expected.getEnvironmentId()) &&
            actual.getGatewayUuid().equals(expected.getGatewayUuid()) &&
            (actual.isFavourite() == expected.isFavourite()) &&
            (actual.isRunOnLoad() == expected.isRunOnLoad()) &&
            (actual.isShowConfirmation() == expected.isShowConfirmation());
    }

    public static DeviceModel equalsTo(DeviceModel expected) {
        return Matchers.argThat(new DeviceModelMatcher(expected));
    }
}