package com.P.G.chatappbackend.cache;

import com.P.G.chatappbackend.dto.OnlineUsers;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class OnlineUserNameCache {

    private Logger logger = Logger.getLogger(OnlineUserNameCache.class.getName());

    private ConcurrentHashMap<String, String> names = new ConcurrentHashMap<>(215);

    public void clearNames() {
        names.clear();
    }

    public void addNewOnlineUser(String username, String sessionId) {
        names.put(sessionId, username);
        logger.log(
                Level.INFO,
                String.format("Client with sessionId %s just connected to the chat room nad has the name %s",
                        sessionId, username));
    }

    public void removeUserFromCache(String sessionId) {
        names.remove(sessionId);
        logger.log(
                Level.INFO,
                String.format("%s just disconnected from the chat room", sessionId));
    }

    public OnlineUsers getOnlineUsers() {
        System.out.println(names.values());
        return new OnlineUsers(new ArrayList<>(names.values()));
    }

    public ConcurrentHashMap<String, String> getNames() {
        return this.names;
    }

}
