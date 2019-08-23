package com.P.G.chatappbackend.dto;

import lombok.Data;

@Data
public class GetRoomKeyResponse {

    private String roomId;

    public GetRoomKeyResponse() {}

    public GetRoomKeyResponse(String roomId) {
        this.roomId = roomId;
    }
}
