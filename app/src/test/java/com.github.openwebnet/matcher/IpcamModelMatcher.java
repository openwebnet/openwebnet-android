package com.github.openwebnet.matcher;

import com.github.openwebnet.model.IpcamModel;
import com.google.common.base.Preconditions;

import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

public class IpcamModelMatcher extends ArgumentMatcher<IpcamModel> {

    private final IpcamModel expected;

    private IpcamModelMatcher(IpcamModel expected) {
        Preconditions.checkNotNull(expected);
        this.expected = expected;
    }

    @Override
    public boolean matches(Object argument) {
        if (argument == null || !(argument instanceof IpcamModel)) {
            return false;
        }
        // ignore uuid
        IpcamModel actual = (IpcamModel) argument;
        return actual.getName().equals(expected.getName()) &&
            actual.getUrl().equals(expected.getUrl()) &&
            actual.getUsername().equals(expected.getUsername()) &&
            actual.getPassword().equals(expected.getPassword()) &&
            actual.getStreamType().equals(expected.getStreamType()) &&
            actual.getEnvironmentId().equals(expected.getEnvironmentId()) &&
            (actual.isFavourite() == expected.isFavourite());
    }

    public static IpcamModel ipcamModelEq(IpcamModel expected) {
        return Matchers.argThat(new IpcamModelMatcher(expected));
    }
}
