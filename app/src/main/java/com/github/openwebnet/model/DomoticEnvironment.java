package com.github.openwebnet.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class DomoticEnvironment extends RealmObject {

    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";

    @PrimaryKey
    private Integer id;

    @Required
    private String name;

    private String description;

    public DomoticEnvironment() {}

    public DomoticEnvironment(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
    }

    /**
     * Prefer this builder to instantiate a new {@link DomoticEnvironment}.
     *
     * Note:
     * {@link lombok.Getter} and {@link lombok.Setter} don't work with {@link RealmObject}
     */
    public static class Builder {

        private final Integer id;
        private final String name;
        private String description;

        public Builder(Integer id, String name) {
            this.id = id;
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

    public static Builder newBuilder(Integer id, String name) {
        return new DomoticEnvironment.Builder(id, name);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
