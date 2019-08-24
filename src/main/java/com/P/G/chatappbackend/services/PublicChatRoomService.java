package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.dto.FirstMessagesResponse;
import com.P.G.chatappbackend.dto.OnlineUsers;
import com.P.G.chatappbackend.dto.PreviousMessagesResponse;
import com.P.G.chatappbackend.models.Message;
import org.bson.types.ObjectId;

public interface PublicChatRoomService {

    Message processMessage(Message message);

    OnlineUsers getListOfCurrentUsers();

    void removeClientFromOnlineUsers(String sessionId);

    PreviousMessagesResponse getNPreviousMessages(ObjectId objectId, int numberOfMessages);

    FirstMessagesResponse getFirstNMessages(int numberOfMessages);

    int getNumberOfCurrentUsers();

    void updateChatRoomWithCurrentUsers();

    Message test(int messPos);

    void addClientToOnlineUsers(String name, String sessionId);

    String giveClientName(String sessionId);

    Message encryptMessage(Message message);

    Message decryptMessage(Message message);

}
