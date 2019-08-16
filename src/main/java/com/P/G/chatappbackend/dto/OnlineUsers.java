package com.P.G.chatappbackend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OnlineUsers {

    private List<String> users = new ArrayList<>();

    public OnlineUsers() {
    }

    public OnlineUsers(List<String> users) {
        this.users = users;
    }
}
