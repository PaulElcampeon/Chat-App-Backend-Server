package com.P.G.chatappbackend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "MESSAGES")
public class Message {

    @Id
    private ObjectId roomId;
    private String sender;
    private String content;
    private long timeSent;

    public Message() {
    }

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;

    }
}
