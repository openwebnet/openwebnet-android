package com.github.openwebnet.model;

import com.github.openwebnet.model.firestore.FirestoreModel;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static com.google.common.base.Preconditions.checkNotNull;

public class GatewayModel extends RealmObject
        implements RealmModel, FirestoreModel<GatewayModel> {

    public static final String FIELD_HOST = "host";
    public static final String FIELD_PORT = "port";
    public static final String FIELD_PASSWORD = "password";

    @Required
    @PrimaryKey
    private String uuid;

    @Required
    private String host;

    @Required
    private Integer port;

    private String password;

    public static GatewayModel newGateway(String host, Integer port, String password) {
        checkNotNull(host, "host is null");
        checkNotNull(port, "port is null");

        GatewayModel gateway = new GatewayModel();
        gateway.setUuid(UUID.randomUUID().toString());
        gateway.setHost(host);
        gateway.setPort(port);
        gateway.setPassword(password);
        return gateway;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_UUID, getUuid());
        map.put(FIELD_HOST, getHost());
        map.put(FIELD_PORT, getPort());
        map.put(FIELD_PASSWORD, getPassword());
        return map;
    }

    @Override
    public GatewayModel fromMap(Map<String, Object> map) {
        checkNotNull(map.get(FIELD_UUID), "uuid is null");
        checkNotNull(map.get(FIELD_HOST), "host is null");
        checkNotNull(map.get(FIELD_PORT), "port is null");
        checkNotNull(map.get(FIELD_PASSWORD), "password is null");

        GatewayModel gateway = new GatewayModel();
        gateway.setUuid((String) map.get(FIELD_UUID));
        gateway.setHost((String) map.get(FIELD_HOST));
        gateway.setPort((Integer) map.get(FIELD_PORT));
        gateway.setPassword((String) map.get(FIELD_PASSWORD));
        return gateway;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordNullable() {
        if (password != null) {
            return Strings.emptyToNull((password.trim().replaceAll("\\s+", "")));
        }
        return null;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
