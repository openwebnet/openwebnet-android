package com.github.openwebnet.model;

import com.github.openwebnet.R;
import com.google.common.collect.Lists;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IpcamModelTest {

    private final Integer IPCAM_ENVIRONMENT = 100;
    private final String IPCAM_NAME = "name";
    private final String IPCAM_URL = "http://ipcamurl";
    private final String IPCAM_USERNAME = "username";
    private final String IPCAM_PASSWORD = "password";
    private final boolean IPCAM_FAVOURITE = true;

    @Test(expected = NullPointerException.class)
    public void testIpcamModelBuilder_nullEnvironmentId() {
        IpcamModel.addBuilder().environment(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testIpcamModelBuilder_nullName() {
        IpcamModel.addBuilder().name(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testIpcamModelBuilder_nullUrl() {
        IpcamModel.addBuilder().url(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testIpcamModelBuilder_nullType() {
        IpcamModel.addBuilder().streamType(null).build();
    }

    @Test
    public void testIpcamModelAddBuilder_success() {
        IpcamModel ipcam = IpcamModel.addBuilder()
            .environment(IPCAM_ENVIRONMENT)
            .name(IPCAM_NAME)
            .url(IPCAM_URL)
            .streamType(IpcamModel.StreamType.MJPEG)
            .favourite(IPCAM_FAVOURITE)
            .username(IPCAM_USERNAME)
            .password(IPCAM_PASSWORD)
            .build();

        assertNotNull("invalid uuid", ipcam.getUuid());
        assertCommonFields(ipcam);
    }

    @Test
    public void testIpcamModelUpdateBuilder_success() {
        String IPCAM_UUID = "myUUid";
        IpcamModel ipcam = IpcamModel.updateBuilder(IPCAM_UUID)
            .environment(IPCAM_ENVIRONMENT)
            .name(IPCAM_NAME)
            .url(IPCAM_URL)
            .streamType(IpcamModel.StreamType.MJPEG)
            .favourite(IPCAM_FAVOURITE)
            .username(IPCAM_USERNAME)
            .password(IPCAM_PASSWORD)
            .build();

        assertEquals("invalid uuid", IPCAM_UUID, ipcam.getUuid());
        assertCommonFields(ipcam);
    }

    private void assertCommonFields(IpcamModel ipcam) {
        assertEquals("invalid environmentId", IPCAM_ENVIRONMENT, ipcam.getEnvironmentId());
        assertEquals("invalid name", IPCAM_NAME, ipcam.getName());
        assertEquals("invalid url", IPCAM_URL, ipcam.getUrl());
        assertEquals("invalid streamType", IpcamModel.StreamType.MJPEG, ipcam.getStreamType());
        assertEquals("invalid favourite", IPCAM_FAVOURITE, ipcam.isFavourite());
        assertEquals("invalid username", IPCAM_USERNAME, ipcam.getUsername());
        assertEquals("invalid password", IPCAM_PASSWORD, ipcam.getPassword());
    }

    @Test
    public void testIpcamModelStreamType() {
        assertEquals("invalid label", "MJPEG", IpcamModel.StreamType.MJPEG.name());
        assertEquals("invalid label", R.string.ipcam_stream_mjpeg, IpcamModel.StreamType.MJPEG.getLabelId());
        assertEquals("invalid label", IpcamModel.StreamType.MJPEG, IpcamModel.StreamType.forName("MJPEG"));
        assertEquals("invalid types", Lists.newArrayList(IpcamModel.StreamType.MJPEG), IpcamModel.StreamType.toList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIpcamModelStreamType_invalid() {
        IpcamModel.StreamType.forName("aaa");
    }

}
