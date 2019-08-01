package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.cache.NameCache;
import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.repositiories.MessageRepository;
import com.P.G.chatappbackend.util.NameCreator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private NameCache nameCache;

    @Autowired
    private NameCreator nameCreator;

    @Override
    public void initializeNameCache() {
        nameCache.setNameCache(nameCreator.createNames());
    }

    @Override
    public String assignUserRandomName(String sessionId) {
        return nameCache.getNameForClient(sessionId);
    }

    @Override
    public void processMessage(Message message) {
        message.setTimeSent(System.currentTimeMillis());
//        messageRepository.insert(message);
    }

    @Override
    public List<String> getListOfCurrentUsers() {
        return nameCache.getListOfActiveUsers();
    }

    @Override
    public void freeUpName(String name) {
        nameCache.freeUpName(name);
    }

    @Override
    public List<Message> getPreviousBatchOfMessages(int i) {
        return messageRepository.findAll().stream().skip(i * 20).limit(20).collect(Collectors.toList());
    }
}
