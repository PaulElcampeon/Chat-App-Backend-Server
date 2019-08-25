package com.P.G.chatappbackend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class MessageConverter {

    private String sender;
    private String content;
    private long timeSent;

    public MessageConverter(@JsonProperty("content") String content,
                            @JsonProperty("sender") String sender,
                            @JsonProperty("timeSent") long timeSent) {
        this.content = content;
        this.sender = sender;
        this.timeSent = timeSent;
    }

    public MessageConverter() {
    }

    public MessageConverter(String sender, String content) {
        this.sender = sender;
        this.content = content;

    }
}
