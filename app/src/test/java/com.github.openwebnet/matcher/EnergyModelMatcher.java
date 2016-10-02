package com.github.openwebnet.matcher;

import com.github.openwebnet.model.EnergyModel;
import com.google.common.base.Preconditions;

import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

public class EnergyModelMatcher extends ArgumentMatcher<EnergyModel> {

    private final EnergyModel expected;

    private EnergyModelMatcher(EnergyModel expected) {
        Preconditions.checkNotNull(expected);
        this.expected = expected;
    }

    @Override
    public boolean matches(Object argument) {
        if (argument == null || !(argument instanceof EnergyModel)) {
            return false;
        }
        // ignore uuid
        EnergyModel actual = (EnergyModel) argument;
        return actual.getName().equals(expected.getName()) &&
            actual.getWhere().equals(expected.getWhere()) &&
            actual.getEnvironmentId().equals(expected.getEnvironmentId()) &&
            actual.getGatewayUuid().equals(expected.getGatewayUuid()) &&
            actual.getEnergyManagementVersion().equals(expected.getEnergyManagementVersion()) &&
            (actual.isFavourite() == expected.isFavourite());
    }

    public static EnergyModel energyModelEq(EnergyModel expected) {
        return Matchers.argThat(new EnergyModelMatcher(expected));
    }
}