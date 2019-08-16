package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.cache.CreateNamesCache;
import com.P.G.chatappbackend.cache.OnlineUserNameCache;
import com.P.G.chatappbackend.dto.FirstMessagesResponse;
import com.P.G.chatappbackend.dto.OnlineUsers;
import com.P.G.chatappbackend.dto.PreviousMessagesResponse;
import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.repositiories.MessageRepository;
import com.P.G.chatappbackend.util.NameCreator;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PublicChatRoomServiceImpl implements PublicChatRoomService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CreateNamesCache createdNamesCache;

    @Autowired
    private OnlineUserNameCache onlineUserNameCache;

    @Autowired
    private NameCreator nameCreator;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void initializeNameCache() {
        createdNamesCache.setNameCache(nameCreator.createMapOfNamesWithAvailability());
    }

    @Override
    public Message processMessage(Message message) {
        message.setTimeSent(System.currentTimeMillis());
        return messageRepository.insert(message);
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
        return new PreviousMessagesResponse(mongoTemplate.find(query, Message.class));
    }

    @Override
    public FirstMessagesResponse getFirstNMessages(int numberOfMessages) {
        Query query = new Query();
        query.limit(numberOfMessages);
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        return new FirstMessagesResponse(mongoTemplate.find(query, Message.class));
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
    public void giveClientName(String sessionId) {
        String name = createdNamesCache.getNameForClient();
        onlineUserNameCache.addNewOnlineUser(name, sessionId);
        simpMessagingTemplate.convertAndSend(String.format("/queue/%s", sessionId), name);
    }

    @Override
    public void deleteAllMessages() {
        messageRepository.deleteAll();
    }
}
