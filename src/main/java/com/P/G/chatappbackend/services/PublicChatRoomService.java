package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.dto.PublicMoreMessagesRequest;

import java.util.List;

public interface PublicChatRoomService {

    void initializeNameCache();

    String assignUserRandomName(String sessionId);

    Message processMessage(Message message);

    List<String> getListOfCurrentUsers();

    void freeUpName(String name);

    List<Message> getPrevious10Messages(PublicMoreMessagesRequest publicMoreMessagesRequest);

    List<Message> getFirst10Messages();

    int getNumberOfCurrentUsers();

    void updateChatroomWithCurrentUsers();
}
