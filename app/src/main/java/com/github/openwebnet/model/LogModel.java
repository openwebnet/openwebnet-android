package com.github.openwebnet.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Required;

import static java.util.Objects.requireNonNull;

public class LogModel extends RealmObject {

    @Required
    private Date timestamp;

    @Required
    private String request;

    @Required
    private String response;

    public LogModel() {}

    private LogModel(String request, String response) {
        this.timestamp = new Date();
        this.request = request;
        this.response = response;
    }

    public static LogModel log(String request, String response) {
        requireNonNull(request, "request is null");
        requireNonNull(response, "response is null");
        return new LogModel(request, response);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
