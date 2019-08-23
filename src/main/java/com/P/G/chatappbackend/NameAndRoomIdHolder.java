package com.P.G.chatappbackend;

import lombok.Data;

@Data
public class NameAndRoomIdHolder {
    private String name;
    private String roomId;

    public NameAndRoomIdHolder() {}

    public NameAndRoomIdHolder(String roomId) {
        this.roomId = roomId;
    }

    public NameAndRoomIdHolder(String name, String roomId) {
        this.name = name;
        this.roomId = roomId;
    }

}
