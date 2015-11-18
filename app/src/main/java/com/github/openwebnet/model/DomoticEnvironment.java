package com.github.openwebnet.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/*
 * {@link lombok.Getter} and {@link lombok.Setter} doesn't work with {@link RealmObject}
 */
public class DomoticEnvironment extends RealmObject {

    public static final String NAME = "name";

    @PrimaryKey
    private String uuid;

    @Required
    private String name;

    private String description;

    public DomoticEnvironment() {}

    private DomoticEnvironment(Builder builder) {
        this.uuid = builder.uuid;
        this.name = builder.name;
        this.description = builder.description;
    }

    public static class Builder {

        private final String uuid;
        private final String name;
        private String description;

        public Builder(String name) {
            this.uuid = UUID.randomUUID().toString();
            this.name = name;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public DomoticEnvironment build() {
            return new DomoticEnvironment(this);
        }
    }

    public static Builder newInstance(String name) {
        return new DomoticEnvironment.Builder(name);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
