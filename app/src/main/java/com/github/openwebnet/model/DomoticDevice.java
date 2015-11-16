package com.github.openwebnet.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * @author niqdev
 */
public class DomoticDevice extends RealmObject {

    @PrimaryKey
    private String uuid;

    @Required
    private String name;

    private String description;

    /* getter|setter (issue: realm doesn't support lombok) */

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
