package com.P.G.chatappbackend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "ROOMS")
public class Room {

    @Id
    private String roomId;
    private List<PrivateChatterInfo> users = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

    public Room() {

    }

    public Room(String roomId) {
        this.roomId = roomId;
    }
}
