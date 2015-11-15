package com.github.openwebnet.model;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * @author niqdev
 *
 * issue: realm doesn't support lombok
 */
public class DomoticDevice extends RealmObject {

    @Required
    private String name;

    private String description;

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
