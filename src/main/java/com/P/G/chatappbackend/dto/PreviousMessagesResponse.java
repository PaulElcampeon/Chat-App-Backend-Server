package com.P.G.chatappbackend.dto;

import com.P.G.chatappbackend.models.Mail;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PreviousMessagesResponse {

    private List<Mail> messages = new ArrayList<>();

    public PreviousMessagesResponse() {

    }

    public PreviousMessagesResponse(List<Mail> messages) {
        this.messages = messages;
    }

}
