package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.cache.CreatedNamesCache;
import com.P.G.chatappbackend.cache.OnlineUserNameCache;
import com.P.G.chatappbackend.dto.FirstMessagesResponse;
import com.P.G.chatappbackend.dto.OnlineUsers;
import com.P.G.chatappbackend.dto.PreviousMessagesResponse;
import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.repositiories.MessageRepository;
import com.P.G.chatappbackend.util.NameCreator;
import org.bson.types.ObjectId;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicChatRoomServiceImpl implements PublicChatRoomService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CreatedNamesCache createdNamesCache;

    @Autowired
    private OnlineUserNameCache onlineUserNameCache;

    @Autowired
    private NameCreator nameCreator;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();

    public PublicChatRoomServiceImpl() {
        basicTextEncryptor.setPassword("secret-password");
    }

    @Override
    public void initializeNameCache() {
        createdNamesCache.setNameCache(nameCreator.createMapOfNamesWithAvailability());
    }

    @Override
    public Message encryptMessage(Message message) {
        message.setContent(basicTextEncryptor.encrypt(message.getContent()));
        return message;
    }

    @Override
    public Message decryptMessage(Message message) {
        message.setContent(basicTextEncryptor.decrypt(message.getContent()));
        return message;
    }

    @Override
    public Message processMessage(Message message) {
        message.setTimeSent(System.currentTimeMillis());
        return messageRepository.insert(encryptMessage(message));
    }

    @Override
    public OnlineUsers getListOfCurrentUsers() {
        return onlineUserNameCache.getOnlineUsers();
    }

    @Override
    public void removeClientFromOnlineUsers(String sessionId) {
        onlineUserNameCache.removeUserFromCache(sessionId);
    }

    @Override
    public PreviousMessagesResponse getNPreviousMessages(ObjectId objectId, int numberOfMessages) {
        Query query = new Query();
        query.addCriteria(Criteria
                .where("_id").lt(objectId));
        query.limit(numberOfMessages);
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        List<Message> decryptedMessages = mongoTemplate.find(query, Message.class).stream().map(this::decryptMessage).collect(Collectors.toList());
        return new PreviousMessagesResponse(decryptedMessages);
    }

    @Override
    public FirstMessagesResponse getFirstNMessages(int numberOfMessages) {
        Query query = new Query();
        query.limit(numberOfMessages);
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        List<Message> decryptedMessages = mongoTemplate.find(query, Message.class).stream().map(this::decryptMessage).collect(Collectors.toList());
        return new FirstMessagesResponse(decryptedMessages);
    }

    @Override
    public int getNumberOfCurrentUsers() {
        return onlineUserNameCache.getOnlineUsers().getUsers().size();
    }

    @Override
    public void updateChatRoomWithCurrentUsers() {
        simpMessagingTemplate.convertAndSend("/topic/public-room/active-users", getListOfCurrentUsers());
    }

    public Message test(int messagePos) {
        return messageRepository.findAll().get(messagePos);
    }

    @Override
    public void addClientToOnlineUsers(String name, String sessionId) {
        onlineUserNameCache.addNewOnlineUser(name, sessionId);
    }

    @Override
    public String giveClientName(String sessionId) {
        String name = createdNamesCache.getNameForClient();
        onlineUserNameCache.addNewOnlineUser(name, sessionId);
        return name;
    }
}
