package com.github.openwebnet.model;

import com.github.niqdev.openwebnet.message.SoundSystem;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SoundModelTest {

    private final Integer SOUND_ENVIRONMENT = 100;
    private final String SOUND_GATEWAY = "gatewayUuid";
    private final String SOUND_NAME = "name";
    private final String SOUND_WHERE = "08";
    private final SoundSystem.Source SOUND_SOURCE = SoundSystem.Source.STEREO_CHANNEL;
    private final SoundSystem.Type SOUND_TYPE = SoundSystem.Type.AMPLIFIER_P2P;
    private final boolean SOUND_FAVOURITE = true;

    @Test(expected = NullPointerException.class)
    public void testSoundModelBuilder_nullEnvironmentId() {
        SoundModel.addBuilder().environment(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testSoundModelBuilder_nullGatewayUuid() {
        SoundModel.addBuilder().gateway(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testSoundModelBuilder_nullName() {
        SoundModel.addBuilder().name(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void testSoundModelBuilder_nullWhere() {
        SoundModel.addBuilder().where(null).build();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSoundModel_getSource() {
        SoundModel sound = new SoundModel();
        sound.getSource();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSoundModel_setSource() {
        SoundModel sound = new SoundModel();
        sound.setSource("SOURCE");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSoundModel_getType() {
        SoundModel sound = new SoundModel();
        sound.getType();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSoundModel_setType() {
        SoundModel sound = new SoundModel();
        sound.setType("TYPE");
    }

    @Test
    public void testSoundModelAddBuilder_success() {
        SoundModel sound = SoundModel.addBuilder()
            .environment(SOUND_ENVIRONMENT)
            .gateway(SOUND_GATEWAY)
            .name(SOUND_NAME)
            .where(SOUND_WHERE)
            .source(SOUND_SOURCE)
            .type(SOUND_TYPE)
            .favourite(SOUND_FAVOURITE)
            .build();

        assertNotNull("invalid uuid", sound.getUuid());
        assertCommonFields(sound);
    }

    @Test
    public void testSoundModelUpdateBuilder_success() {
        String SOUND_UUID = "myUUid";
        SoundModel sound = SoundModel.updateBuilder(SOUND_UUID)
            .environment(SOUND_ENVIRONMENT)
            .gateway(SOUND_GATEWAY)
            .name(SOUND_NAME)
            .where(SOUND_WHERE)
            .source(SOUND_SOURCE)
            .type(SOUND_TYPE)
            .favourite(SOUND_FAVOURITE)
            .build();

        assertEquals("invalid uuid", SOUND_UUID, sound.getUuid());
        assertCommonFields(sound);
    }

    private void assertCommonFields(SoundModel sound) {
        assertEquals("invalid environmentId", SOUND_ENVIRONMENT, sound.getEnvironmentId());
        assertEquals("invalid gatewayUuid", SOUND_GATEWAY, sound.getGatewayUuid());
        assertEquals("invalid name", SOUND_NAME, sound.getName());
        assertEquals("invalid where", SOUND_WHERE, sound.getWhere());
        assertEquals("invalid source", SOUND_SOURCE, sound.getSoundSystemSource());
        assertEquals("invalid type", SOUND_TYPE, sound.getSoundSystemType());
        assertEquals("invalid favourite", SOUND_FAVOURITE, sound.isFavourite());
        assertNull("invalid status", sound.getStatus());
    }

}
