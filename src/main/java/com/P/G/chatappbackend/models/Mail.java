package com.P.G.chatappbackend.models;

import com.P.G.chatappbackend.dto.RoomMessage;
import com.P.G.chatappbackend.enums.MessageType;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "MAILS")
public class Mail extends RoomMessage {

    @Id
    private ObjectId id;
    private String roomId;
    private String sender;
    private String content;
    private long timeSent;

    public Mail() {
        super(MessageType.MAIL);
    }

    public Mail(String sender, String content, String roomId, long timeSent) {
        super(MessageType.MAIL);
        this.sender = sender;
        this.content = content;
        this.roomId = roomId;
        this.timeSent = timeSent;
    }
}
