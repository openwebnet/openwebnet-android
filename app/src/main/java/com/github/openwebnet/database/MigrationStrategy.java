package com.github.openwebnet.database;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

import static com.github.openwebnet.model.AutomationModel.FIELD_WHERE;
import static com.github.openwebnet.model.DomoticModel.FIELD_ENVIRONMENT_ID;
import static com.github.openwebnet.model.DomoticModel.FIELD_FAVOURITE;
import static com.github.openwebnet.model.DomoticModel.FIELD_GATEWAY_UUID;
import static com.github.openwebnet.model.DomoticModel.FIELD_NAME;
import static com.github.openwebnet.model.RealmModel.FIELD_UUID;

public class MigrationStrategy implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();

        // Migrate to version 2: Add the AutomationModel class.
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
    }
}
