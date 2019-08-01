package com.P.G.chatappbackend.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "MESSAGES")
public class Message {

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
