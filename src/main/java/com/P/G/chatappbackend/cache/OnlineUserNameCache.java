package com.P.G.chatappbackend.cache;

import com.P.G.chatappbackend.NameAndRoomIdHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class OnlineUserNameCache {

    private Logger logger = Logger.getLogger(OnlineUserNameCache.class.getName());

    private ConcurrentHashMap<String, NameAndRoomIdHolder> names = new ConcurrentHashMap<>(1000);

    public void clearNames() {
        names.clear();
    }

    public void addNewOnlineUser(NameAndRoomIdHolder nameAndRoomIdHolder, String sessionId) {
        names.put(sessionId, nameAndRoomIdHolder);
        logger.log(
                Level.INFO,
                String.format("Client with sessionId:%s name:%s just connected to a chat room with id:%s",
                        sessionId, nameAndRoomIdHolder.getName(), nameAndRoomIdHolder.getRoomId()));
        logNumberOfUsersOnline();
    }

    public NameAndRoomIdHolder removeUserFromCache(String sessionId) {
        NameAndRoomIdHolder nameAndRoomIdHolder = names.get(sessionId);
        names.remove(sessionId);
        logger.log(
                Level.INFO,
                String.format("%s just disconnected from the chat room", sessionId));
        logNumberOfUsersOnline();
        return nameAndRoomIdHolder;
    }

    public ConcurrentHashMap<String, NameAndRoomIdHolder> getNames() {
        return this.names;
    }

    public void logNumberOfUsersOnline() {
        logger.log(
                Level.INFO,
                String.format("%d users currently online", names.size()));
    }
}
