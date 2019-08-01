package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.models.Message;

import java.util.List;

public interface ChatRoomService {

    void initializeNameCache();

    String assignUserRandomName(String sessionId);

    void processMessage(Message message);

    List<String> getListOfCurrentUsers();

    void freeUpName(String name);

    List<Message> getPreviousBatchOfMessages(int i);
}
