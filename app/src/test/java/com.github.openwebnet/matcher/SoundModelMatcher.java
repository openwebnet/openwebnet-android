package com.github.openwebnet.matcher;

import com.github.openwebnet.model.SoundModel;
import com.google.common.base.Preconditions;

import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

public class SoundModelMatcher extends ArgumentMatcher<SoundModel> {

    private final SoundModel expected;

    private SoundModelMatcher(SoundModel expected) {
        Preconditions.checkNotNull(expected);
        this.expected = expected;
    }

    @Override
    public boolean matches(Object argument) {
        if (argument == null || !(argument instanceof SoundModel)) {
            return false;
        }
        // ignore uuid
        SoundModel actual = (SoundModel) argument;
        return actual.getName().equals(expected.getName()) &&
            actual.getWhere().equals(expected.getWhere()) &&
            actual.getSoundSystemSource().equals(expected.getSoundSystemSource()) &&
            actual.getSoundSystemType().equals(expected.getSoundSystemType()) &&
            actual.getEnvironmentId().equals(expected.getEnvironmentId()) &&
            actual.getGatewayUuid().equals(expected.getGatewayUuid()) &&
            (actual.isFavourite() == expected.isFavourite());
    }

    public static SoundModel soundModelEq(SoundModel expected) {
        return Matchers.argThat(new SoundModelMatcher(expected));
    }
}