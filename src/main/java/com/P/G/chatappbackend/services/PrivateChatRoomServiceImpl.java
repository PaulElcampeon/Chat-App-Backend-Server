package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.models.MoreMessagesRequest;
import com.P.G.chatappbackend.models.Room;
import com.P.G.chatappbackend.repositiories.PrivateChatRoomRepository;
import com.P.G.chatappbackend.util.NameCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PrivateChatRoomServiceImpl implements PrivateChatRoomService {

    @Autowired
    private PrivateChatRoomRepository privateChatRoomRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private NameCreator nameCreator;

    @Override
    public String createRoom() {
        String roomId =  Base64.getEncoder().encodeToString(String.valueOf(System.currentTimeMillis()).getBytes());
        Room room = new Room(roomId);
        privateChatRoomRepository.insert(room);
        room.getNameCache().setNameCache(nameCreator.createNames());
        return roomId;
    }

    @Override
    public Message processMessage(Message message, String roomId) {
        return null;
    }

    @Override
    public String getName(String sessionId, String roomId) {
        Room room = privateChatRoomRepository.findById(roomId).orElseThrow(NoSuchElementException::new);
        String name =  room.getNameCache().getNameForClient(sessionId);
        updateRoom(room);
        return name;
    }

    @Override
    public Room getRoom(String roomId) {
        return null;
    }

    @Override
    public List<Message> getPrevious10Messages(MoreMessagesRequest moreMessagesRequest, String roomId) {
        System.out.println(moreMessagesRequest);
        System.out.println(roomId);
        return null;
    }

    @Override
    public List<Message> getLatest10Messages(String roomId) {
        return null;
    }

    @Override
    public List<String> getActiveUsers(String roomId) {
        Room room = privateChatRoomRepository.findById(roomId).orElseThrow(NoSuchElementException::new);
        return room.getNameCache().getListOfActiveUsers();
    }

    @Override
    public void updateRoom(Room room) {
        privateChatRoomRepository.save(room);
    }


}
