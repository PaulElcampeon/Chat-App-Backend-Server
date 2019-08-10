package com.P.G.chatappbackend.dto;

import lombok.Data;

import org.bson.types.ObjectId;

@Data
public class PublicMoreMessagesRequest {

    private ObjectId messageId;

    public PublicMoreMessagesRequest() {}

    public PublicMoreMessagesRequest(ObjectId messageId) {
        this.messageId = messageId;
    }

}
