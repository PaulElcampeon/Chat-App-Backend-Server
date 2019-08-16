package com.P.G.chatappbackend.dto;

import com.P.G.chatappbackend.models.Message;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FirstMessagesResponse {

    private List<Message> messages = new ArrayList<>();

    public FirstMessagesResponse() {
    }

    public FirstMessagesResponse(List<Message> messages) {
        this.messages = messages;
    }
}
