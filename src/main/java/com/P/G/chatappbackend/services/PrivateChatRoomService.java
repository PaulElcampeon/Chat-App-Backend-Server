package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.dto.PrivateMoreMessageRequest;
import com.P.G.chatappbackend.models.Room;

import java.util.List;

public interface PrivateChatRoomService {

    String createRoom();

    Message processMessage(Message message, String roomId);

    String getName(String sessionId, String roomId);

    Room getRoom(String roomId);

    List<Message> getPrevious10Messages(PrivateMoreMessageRequest privateMoreMessageRequest, String roomId);

    List<Message> getLatest10Messages(String roomId);

    List<String> getActiveUsers(String roomId);

    void updateRoom(Room room);

    void handlePlayerDisconnection(String sessionId, String roomId);

    String getRandomName();

    void updateSessionId(String name, String sessionId,String roomId);
}
