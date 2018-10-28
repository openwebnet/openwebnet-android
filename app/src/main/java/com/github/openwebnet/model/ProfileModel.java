package com.github.openwebnet.model;

import org.threeten.bp.ZonedDateTime;

import static com.google.common.base.Preconditions.checkNotNull;

public class ProfileModel {

    private String name;

    private ZonedDateTime dateTime;

    private ProfileModel(Builder builder) {
        this.name = builder.name;
        this.dateTime = builder.dateTime;
    }

    public static class Builder {

        private String name;
        private ZonedDateTime dateTime;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder dateTime(ZonedDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public ProfileModel build() {
            checkNotNull(name, "name is null");
            checkNotNull(dateTime, "dateTime is null");

            return new ProfileModel(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

}
