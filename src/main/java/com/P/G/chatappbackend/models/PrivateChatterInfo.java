package com.P.G.chatappbackend.models;

import lombok.Data;

@Data
public class PrivateChatterInfo {

    private String name;
    private String sessionId;
    private boolean online;

    public PrivateChatterInfo() {}

    public PrivateChatterInfo(String name, String sessionId) {
        this.name = name;
        this.sessionId = sessionId;
    }

    public PrivateChatterInfo(String name, String sessionId, boolean online) {
        this.name = name;
        this.sessionId = sessionId;
        this.online = online;
    }
}
