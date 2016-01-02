package com.github.openwebnet.model;

/**
 * From <a href="https://realm.io/docs/java/latest/#queries">documentation</a>:
 * <br>
 * due to how the proxy classes override getters and setters in the model classes
 * there are some restrictions to what is allowed in a model class:
 * <ul>
 *     <li>Only private instance fields.</li>
 *     <li>Only default getter and setter methods.</li>
 *     <li>Static fields, both public and private.</li>
 *     <li>Static methods.</li>
 *     <li>Implementing interfaces with no methods.</li>
 * </ul>
 * This means that it is currently not possible to extend anything else than RealmObject
 * or to override methods like toString() or equals().
 * Also it is only possible to implement interfaces.
 */
public interface RealmModel {

    String FIELD_UUID = "uuid";

    String getUuid();

}
