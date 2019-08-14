package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.dto.ActiveUsersResponse;
import com.P.G.chatappbackend.dto.FirstMessagesResponse;
import com.P.G.chatappbackend.dto.PreviousMessagesResponse;
import com.P.G.chatappbackend.models.Message;
import org.bson.types.ObjectId;

public interface PublicChatRoomService {

    void initializeNameCache();

    String assignUserRandomName(String sessionId);

    Message processMessage(Message message);

    ActiveUsersResponse getListOfCurrentUsers();

    void freeUpName(String name);

    PreviousMessagesResponse getNPreviousMessages(ObjectId objectId, int numberOfMessages);

    FirstMessagesResponse getFirstNMessages(int numberOfMessages);

    int getNumberOfCurrentUsers();

    void updateChatroomWithCurrentUsers();

    void deleteAllMessages();

    Message test(int messPos);
}
