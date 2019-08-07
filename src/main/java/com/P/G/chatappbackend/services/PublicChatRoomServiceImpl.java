package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.cache.NameCache;
import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.models.MoreMessagesRequest;
import com.P.G.chatappbackend.repositiories.MessageRepository;
import com.P.G.chatappbackend.util.NameCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PublicChatRoomServiceImpl implements PublicChatRoomService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private NameCache nameCache;

    @Autowired
    private NameCreator nameCreator;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void initializeNameCache() {
        nameCache.setNameCache(nameCreator.createNames());
    }

    @Override
    public String assignUserRandomName(String sessionId) {
        return nameCache.getNameForClient(sessionId);
    }

    @Override
    public Message processMessage(Message message) {
        message.setTimeSent(System.currentTimeMillis());
        return messageRepository.insert(message);
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
    public List<Message> getPrevious10Messages(MoreMessagesRequest moreMessagesRequest) {
        return messageRepository.findFirst10By_idLessThan(moreMessagesRequest.getMessageId());
    }

    @Override
    public List<Message> getFirst10Messages() {
        List<Message> messages = messageRepository.findFirst10ByOrderByTimeSentDesc();
        Collections.reverse(messages);
        return messages;
    }

    @Override
    public int getNumberOfCurrentUsers() {
        return nameCache.getNumberOfActiveUsers();
    }

    @Override
    public void updateChatroomWithCurrentUsers() {
        simpMessagingTemplate.convertAndSend("/topic/public-room/current-users", getListOfCurrentUsers());
    }


}
