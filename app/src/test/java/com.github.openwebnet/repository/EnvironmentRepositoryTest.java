package com.github.openwebnet.repository;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.observers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class EnvironmentRepositoryTest {

    @Mock
    Context mMockContext;

    @Test
    public void testNextId() {
        TestSubscriber<Integer> tester = new TestSubscriber<>();
    }
}
