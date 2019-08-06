package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.models.MoreMessagesRequest;

import java.util.List;

public interface ChatRoomService {

    void initializeNameCache();

    String assignUserRandomName(String sessionId);

    Message processMessage(Message message);

    List<String> getListOfCurrentUsers();

    void freeUpName(String name);

    List<Message> getPrevious10Messages(MoreMessagesRequest moreMessagesRequest);

    List<Message> getFirst10Messages();

    int getNumberOfCurrentUsers();

    void updateChatroomWithCurrentUsers();
}
