package com.P.G.chatappbackend.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "MAILS")
public class Mail {

    @Id
    private ObjectId id;
    private String roomId;
    private String sender;
    private String content;
    private long timeSent;

    public Mail() {
    }

    public Mail(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public Mail(String sender, String content, String roomId, long timeSent) {
        this.sender = sender;
        this.content = content;
        this.roomId = roomId;
        this.timeSent = timeSent;
    }
}
