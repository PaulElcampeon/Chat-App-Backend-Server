package com.P.G.chatappbackend.dto;

import com.P.G.chatappbackend.models.Message;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PreviousMessagesResponse {

    private List<Message> messages = new ArrayList<>();

    public PreviousMessagesResponse() {
    }

    public PreviousMessagesResponse(List<Message> messages) {
        this.messages = messages;
    }

}
