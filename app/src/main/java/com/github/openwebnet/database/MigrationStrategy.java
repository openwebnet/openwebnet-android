package com.github.openwebnet.database;

import com.github.openwebnet.model.GatewayModel;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

import static com.github.openwebnet.model.DomoticModel.FIELD_ENVIRONMENT_ID;
import static com.github.openwebnet.model.DomoticModel.FIELD_FAVOURITE;
import static com.github.openwebnet.model.DomoticModel.FIELD_GATEWAY_UUID;
import static com.github.openwebnet.model.DomoticModel.FIELD_NAME;
import static com.github.openwebnet.model.DomoticModel.FIELD_WHERE;
import static com.github.openwebnet.model.EnergyModel.FIELD_VERSION;
import static com.github.openwebnet.model.IpcamModel.FIELD_PASSWORD;
import static com.github.openwebnet.model.IpcamModel.FIELD_STREAM_TYPE;
import static com.github.openwebnet.model.IpcamModel.FIELD_URL;
import static com.github.openwebnet.model.IpcamModel.FIELD_USERNAME;
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
                .addField(FIELD_URL, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_STREAM_TYPE, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_USERNAME, String.class)
                .addField(FIELD_PASSWORD, String.class)
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
                .addField(FIELD_VERSION, String.class, FieldAttribute.REQUIRED)
                .addField(FIELD_FAVOURITE, boolean.class);

            ++oldVersion;
        }

    }
}
