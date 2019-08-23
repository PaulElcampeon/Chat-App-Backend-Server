package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.NameAndRoomIdHolder;
import com.P.G.chatappbackend.dto.FirstMessagesResponse;
import com.P.G.chatappbackend.dto.GetRoomKeyResponse;
import com.P.G.chatappbackend.dto.OnlineUsers;
import com.P.G.chatappbackend.dto.PreviousMessagesResponse;
import com.P.G.chatappbackend.models.Mail;
import com.P.G.chatappbackend.models.MailId;
import org.bson.types.ObjectId;

public interface RoomService {

    void initializeNameCache();

    GetRoomKeyResponse getRoomKey();

    Mail processMessage(Mail message);

    Mail encryptMessage(Mail message);

    Mail decryptMessage(Mail message);

    NameAndRoomIdHolder removeClientFromOnlineUsers(String sessionId);

    PreviousMessagesResponse getNPreviousMessages(ObjectId objectId, String roomId, int numberOfMessagesWanted);

    FirstMessagesResponse getLatestNMessages(String roomId, int numberOfMessages);

    void notifyChatRoomOfCurrentUsers(String roomId);

    void addClientToPrivateRoom(NameAndRoomIdHolder nameAndRoomIdHolder);

    void removeUserFromPrivateRoom(NameAndRoomIdHolder nameAndRoomIdHolder);

    void addClientToOnlineUsers(NameAndRoomIdHolder nameAndRoomIdHolder, String sessionId);

    String giveClientName();
}
