package com.P.G.chatappbackend.models;

import lombok.Data;

import org.bson.types.ObjectId;

@Data
public class MoreMessagesRequest {

    private ObjectId messageId;
}
