package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.NameAndRoomIdHolder;
import com.P.G.chatappbackend.cache.CreateNamesCache;
import com.P.G.chatappbackend.cache.OnlineUserNameCache;
import com.P.G.chatappbackend.dto.FirstMessagesResponse;
import com.P.G.chatappbackend.dto.GetRoomKeyResponse;
import com.P.G.chatappbackend.dto.OnlineUsers;
import com.P.G.chatappbackend.dto.PreviousMessagesResponse;
import com.P.G.chatappbackend.models.Mail;
import com.P.G.chatappbackend.models.MailId;
import com.P.G.chatappbackend.models.Room;
import com.P.G.chatappbackend.repositiories.RoomRepository;
import com.P.G.chatappbackend.util.NameCreator;
import org.bson.types.ObjectId;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

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

    private BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();

    private Logger logger = Logger.getLogger(RoomServiceImpl.class.getName());

    public RoomServiceImpl() {
        basicTextEncryptor.setPassword("secret-password");
    }

    @Override
    public GetRoomKeyResponse getRoomKey() {
        long roomCount = roomRepository.count();
        long counter = 0;
        while (true) {
            counter++;
            String roomId = UUID.randomUUID().toString();
            if (!roomRepository.existsById(roomId)) {
                roomRepository.insert(new Room(roomId));
                return new GetRoomKeyResponse(roomId);
            }
            if (counter == roomCount) {
                return new GetRoomKeyResponse();
            }
        }
    }

    @Override
    public void initializeNameCache() {
        createdNamesCache.setNameCache(nameCreator.createMapOfNamesWithAvailability());
    }

    @Override
    public Mail processMessage(Mail mail) {
        mail.setTimeSent(System.currentTimeMillis());

        mongoTemplate.save(encryptMessage(mail), "MAILS");

        logger.log(Level.INFO, String.format("Mail %s added to room with id:%s", mail, mail.getRoomId()));

        return decryptMessage(mail);
    }

    @Override
    public Mail encryptMessage(Mail message) {
        message.setContent(basicTextEncryptor.encrypt(message.getContent()));
        return message;
    }

    @Override
    public Mail decryptMessage(Mail message) {
        message.setContent(basicTextEncryptor.decrypt(message.getContent()));
        return message;
    }

    @Override
    public PreviousMessagesResponse getNPreviousMessages(ObjectId objectId, String roomId,int numberOfMessagesWanted) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId).and("_id").lt(objectId));
        query.limit(numberOfMessagesWanted);
        query.with(new Sort(Sort.Direction.DESC, "_id"));

        List<Mail> decryptedMessages = mongoTemplate.find(query, Mail.class).stream().map(this::decryptMessage).collect(Collectors.toList());

        return new PreviousMessagesResponse(decryptedMessages);
    }

    @Override
    public FirstMessagesResponse getLatestNMessages(String roomId, int numberOfMessages) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId));
        query.limit(numberOfMessages);
        query.with(new Sort(Sort.Direction.DESC, "_id"));

        List<Mail> decryptedMessages = mongoTemplate.find(query, Mail.class).stream().map(this::decryptMessage).collect(Collectors.toList());

        return new FirstMessagesResponse(decryptedMessages);
    }

    @Override
    public void notifyChatRoomOfCurrentUsers(String roomId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId));
        query.fields().include("users");

        simpMessagingTemplate.convertAndSend(String.format("/topic/room/%s", roomId), new OnlineUsers(mongoTemplate.findOne(query, Room.class).getUsers()));
    }

    @Override
    public void addClientToPrivateRoom(NameAndRoomIdHolder nameAndRoomIdHolder) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(nameAndRoomIdHolder.getRoomId()));

        Update update = new Update();
        update.addToSet("users", nameAndRoomIdHolder.getName());

        mongoTemplate.updateFirst(query, update, Room.class);
    }

    @Override
    public void removeUserFromPrivateRoom(NameAndRoomIdHolder nameAndRoomIdHolder) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(nameAndRoomIdHolder.getRoomId()));

        Update update = new Update();
        update.pull("users", nameAndRoomIdHolder.getName());

        mongoTemplate.updateFirst(query, update, Room.class);
    }

    @Override
    public void addClientToOnlineUsers(NameAndRoomIdHolder nameAndRoomIdHolder, String sessionId) {
        onlineUserNameCache.addNewOnlineUser(nameAndRoomIdHolder, sessionId);
    }

    @Override
    public NameAndRoomIdHolder removeClientFromOnlineUsers(String sessionId) {
        return onlineUserNameCache.removeUserFromCache(sessionId);
    }

    @Override
    public String giveClientName() {
        return createdNamesCache.getNameForClient();
    }
}
