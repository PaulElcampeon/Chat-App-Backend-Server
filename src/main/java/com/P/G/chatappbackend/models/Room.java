package com.P.G.chatappbackend.models;

import com.P.G.chatappbackend.cache.NameCache;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "ROOMS")
public class Room {

    @Id
    private String _id;
    private Map<String, String> users = new HashMap<>();
    private List<Message> messages = new ArrayList<>();
    private NameCache nameCache = new NameCache();

    public Room() {

    }


    public Room(String _id) {
        this._id = _id;
    }
}
