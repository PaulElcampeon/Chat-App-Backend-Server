package com.P.G.chatappbackend.dto;

import lombok.Data;

@Data
public class NameRequest {

    private String name;

    public NameRequest() {}

    public NameRequest(String name) {
        this.name = name;
    }
}
