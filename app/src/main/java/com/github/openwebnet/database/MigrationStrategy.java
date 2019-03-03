package com.github.openwebnet.database;

import com.github.niqdev.openwebnet.message.Automation;
import com.github.niqdev.openwebnet.message.Lighting;
import com.github.openwebnet.model.AutomationModel;
import com.github.openwebnet.model.EnergyModel;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.model.IpcamModel;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.model.SoundModel;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

import static com.github.openwebnet.model.DomoticModel.FIELD_ENVIRONMENT_ID;
import static com.github.openwebnet.model.DomoticModel.FIELD_FAVOURITE;
import static com.github.openwebnet.model.DomoticModel.FIELD_GATEWAY_UUID;
import static com.github.openwebnet.model.DomoticModel.FIELD_NAME;
import static com.github.openwebnet.model.DomoticModel.FIELD_WHERE;
import static com.github.openwebnet.model.RealmModel.FIELD_UUID;

public class MigrationStrategy implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();

        // migrate to version 2
        if (oldVersion == 1) {
            schema.create("AutomationModel")
                .addField(FIELD_UUID, String.class, FieldAttribute.PRIMARY_KEY)
                .addField(FIELD_ENVIRONMENT_ID, Integer.class, FieldAttribute.REQUIRED)
                .addField(FIELD_GATEWAY_UUID, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_NAME, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_WHERE, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_FAVOURITE, boolean.class);

            ++oldVersion;
        }

        // migrate to version 3
        if (oldVersion == 2) {
            schema.create("IpcamModel")
                .addField(FIELD_UUID, String.class, FieldAttribute.PRIMARY_KEY)
                .addField(FIELD_ENVIRONMENT_ID, Integer.class, FieldAttribute.REQUIRED)
                .addField(FIELD_NAME, String.class, FieldAttribute.REQUIRED)
                .addField(IpcamModel.FIELD_URL, String.class, FieldAttribute.REQUIRED)
                .addField(IpcamModel.FIELD_STREAM_TYPE, String.class, FieldAttribute.REQUIRED)
                .addField(IpcamModel.FIELD_USERNAME, String.class)
                .addField(IpcamModel.FIELD_PASSWORD, String.class)
                .addField(FIELD_FAVOURITE, boolean.class);

            ++oldVersion;
        }

        // migrate to version 4
        if (oldVersion == 3) {
            schema.create("TemperatureModel")
                .addField(FIELD_UUID, String.class, FieldAttribute.PRIMARY_KEY)
                .addField(FIELD_ENVIRONMENT_ID, Integer.class, FieldAttribute.REQUIRED)
                .addField(FIELD_GATEWAY_UUID, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_NAME, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_WHERE, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_FAVOURITE, boolean.class);

            ++oldVersion;
        }

        // migrate to version 5
        if (oldVersion == 4) {
            schema.get("GatewayModel")
                .addField(GatewayModel.FIELD_PASSWORD, String.class);

            ++oldVersion;
        }

        // migrate to version 6
        if (oldVersion == 5) {
            schema.create("ScenarioModel")
                .addField(FIELD_UUID, String.class, FieldAttribute.PRIMARY_KEY)
                .addField(FIELD_ENVIRONMENT_ID, Integer.class, FieldAttribute.REQUIRED)
                .addField(FIELD_GATEWAY_UUID, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_NAME, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_WHERE, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_FAVOURITE, boolean.class);

            ++oldVersion;
        }

        // migrate to version 7
        if (oldVersion == 6) {
            schema.create("EnergyModel")
                .addField(FIELD_UUID, String.class, FieldAttribute.PRIMARY_KEY)
                .addField(FIELD_ENVIRONMENT_ID, Integer.class, FieldAttribute.REQUIRED)
                .addField(FIELD_GATEWAY_UUID, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_NAME, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_WHERE, String.class, FieldAttribute.REQUIRED)
                .addField(EnergyModel.FIELD_VERSION, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_FAVOURITE, boolean.class);

            ++oldVersion;
        }

        // migrate to version 8
        if (oldVersion == 7) {
            schema.get("LightModel")
                .addField(LightModel.FIELD_TYPE, String.class, FieldAttribute.REQUIRED)
                .transform(obj -> obj.set(LightModel.FIELD_TYPE, Lighting.Type.POINT_TO_POINT.name()));

            ++oldVersion;
        }

        // migrate to version 9
        if (oldVersion == 8) {
            schema.create("SoundModel")
                .addField(FIELD_UUID, String.class, FieldAttribute.PRIMARY_KEY)
                .addField(FIELD_ENVIRONMENT_ID, Integer.class, FieldAttribute.REQUIRED)
                .addField(FIELD_GATEWAY_UUID, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_NAME, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_WHERE, String.class, FieldAttribute.REQUIRED)
                .addField(SoundModel.FIELD_SOURCE, String.class, FieldAttribute.REQUIRED)
                .addField(SoundModel.FIELD_TYPE, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_FAVOURITE, boolean.class);

            ++oldVersion;
        }

        // migrate to version 10
        if (oldVersion == 9) {
            schema.get("LightModel")
                .removeField(LightModel.FIELD_DIMMER);

            ++oldVersion;
        }

        // migrate to version 11
        if (oldVersion == 10) {
            schema.get("AutomationModel")
                .addField(AutomationModel.FIELD_TYPE, String.class, FieldAttribute.REQUIRED)
                .transform(obj -> obj.set(AutomationModel.FIELD_TYPE, Automation.Type.POINT.name()))
                .addField(AutomationModel.FIELD_BUS, String.class, FieldAttribute.REQUIRED)
                .transform(obj -> obj.set(AutomationModel.FIELD_BUS, Automation.NO_BUS));

            schema.get("LightModel")
                .addField(LightModel.FIELD_BUS, String.class, FieldAttribute.REQUIRED)
                .transform(obj -> obj.set(LightModel.FIELD_BUS, Lighting.NO_BUS));

            ++oldVersion;
        }

    }
}
