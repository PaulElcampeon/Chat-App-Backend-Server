package com.P.G.chatappbackend.dto;

import com.P.G.chatappbackend.models.Mail;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FirstMessagesResponse {

    private List<Mail> messages = new ArrayList<>();

    public FirstMessagesResponse() {
    }

    public FirstMessagesResponse(List<Mail> messages) {
        this.messages = messages;
    }
}
