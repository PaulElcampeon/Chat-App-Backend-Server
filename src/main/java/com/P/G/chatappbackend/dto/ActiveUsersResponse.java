package com.P.G.chatappbackend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ActiveUsersResponse {

    private List<String> users = new ArrayList<>();

    public ActiveUsersResponse() {}

    public ActiveUsersResponse(List<String> users) {
        this.users = users;
    }
}
