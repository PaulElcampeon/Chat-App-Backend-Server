package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.dto.PrivateMoreMessageRequest;
import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.models.PrivateChatterInfo;
import com.P.G.chatappbackend.models.Room;
import com.P.G.chatappbackend.repositiories.PrivateChatRoomRepository;
import com.P.G.chatappbackend.util.NameCreator;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class PrivateChatRoomServiceImpl implements PrivateChatRoomService {

    @Autowired
    private PrivateChatRoomRepository privateChatRoomRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private NameCreator nameCreator;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final ConcurrentSkipListSet<String> nameTracker = new ConcurrentSkipListSet<>();

    private List<String> possibleNames = nameCreator.createNamesList();

    private Logger logger = Logger.getLogger(PrivateChatRoomServiceImpl.class.getName());

    @Override
    public String createRoom() {
        UUID uuid = UUID.randomUUID();
        String roomId = uuid.toString();

        Room room = new Room(roomId);

        privateChatRoomRepository.insert(room);

        logger.log(Level.INFO, String.format("Room with id:%s created", roomId));

        return roomId;
    }

    @Override
    public Message processMessage(Message message, String roomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(roomId));

        Update update = new Update();
        update.addToSet("messages", message);

        mongoTemplate.updateFirst(query, update, Room.class);

        logger.log(Level.INFO, String.format("Message %s added to room with id:%s", message, roomId));

        return message;
    }

    @Override
    public String getName(String sessionId, String roomId) {
        String name = getRandomName();

        while (!nameTracker.add(roomId.concat(".").concat(name))) {
            name = getRandomName();
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(roomId));

        Update update = new Update();

        //Will only add to set if no other duplicate is there
        update.addToSet("users", new PrivateChatterInfo(name, sessionId, true));

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Room.class);

        logger.log(Level.INFO, String.format("Client with sessionId:%s was given the name %s", sessionId, name));

        logger.log(Level.INFO, String.format("Client with sessionId:%s requested for a name in the room with id:%s modified:%d", roomId, updateResult.getModifiedCount()));

        return name;
    }

    @Override
    public Room getRoom(String roomId) {
        return privateChatRoomRepository.findById(roomId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<Message> getPrevious10Messages(PrivateMoreMessageRequest privateMoreMessageRequest, String roomId) {
        return privateChatRoomRepository
                .findById(roomId)
                .orElseThrow(NoSuchElementException::new)
                .getMessages()
                .stream()
                .filter(message -> message.getTimeSent() < privateMoreMessageRequest.getTimeSent())
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> getLatest10Messages(String roomId) {
        List<Message> messages = privateChatRoomRepository
                .findById(roomId)
                .orElseThrow(NoSuchElementException::new)
                .getMessages();

        Collections.reverse(messages);

        return messages
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getActiveUsers(String roomId) {
        return privateChatRoomRepository
                .findById(roomId)
                .orElseThrow(NoSuchElementException::new)
                .getUsers()
                .stream()
                .filter(PrivateChatterInfo::isOnline)
                .map(PrivateChatterInfo::getName)
                .collect(Collectors.toList());
    }

    @Override
    public void updateRoom(Room room) {
        privateChatRoomRepository.save(room);
    }

    @Override
    public void handlePlayerDisconnection(String sessionId, String roomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(roomId).and("users.sessionId").is(sessionId));

        Update update = new Update();
        update.set("users.$.online", true);

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Room.class);

        logger.log(Level.INFO, String.format("Client with session id:%s just disconnected from the private chat room with id:%s", sessionId, roomId));
    }

    @Override
    public String getRandomName() {
        return possibleNames.get(ThreadLocalRandom.current().nextInt(0, possibleNames.size() - 1));
    }

    @Override
    public void updateSessionId(String name, String sessionId, String roomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(roomId).and("users.name").is(name));

        Update update = new Update();
        update.set("users.$.sessionId", sessionId);

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Room.class);

        logger.log(Level.INFO, String.format("Attempted to update session for users with name:%s in room with id:%s %nUpdate result modified count:%d ", name, roomId, updateResult.getModifiedCount()));
    }

}
