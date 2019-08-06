package com.P.G.chatappbackend.cache;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component()
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class NameCache {

    private ConcurrentHashMap<String, String> names;

    private Logger logger = Logger.getLogger(NameCache.class.getName());

    public ConcurrentHashMap<String, String> getNames() {
        return names;
    }

    public void setNameCache(ConcurrentHashMap<String, String> concurrentHashMap) {
        this.names = concurrentHashMap;
    }

    public String getNameForClient(String sessionId) {
        for (String key : names.keySet()) {
            synchronized (this) {
                if (names.get(key).equals("")) {
                    names.put(key, sessionId);
                    logger.log(Level.INFO, String.format("Current status of name cache: %s", getNames()));
                    return key;
                }
            }
        }
        return null;
    }

    public void freeUpName(String sessionId) {
        names.forEach((key, value) -> {
            if (value.equals(sessionId)) {
                names.put(key, "");
                logger.log(Level.INFO, String.format("The name %s has just been freed up by user with session id:%s", key, sessionId));
            }
        });
        logger.log(Level.INFO, String.format("Current status of name cache: %s", getNames()));
    }

    public List<String> getListOfActiveUsers() {
        return names.keySet().stream().filter(key -> names.get(key).length() > 1).collect(Collectors.toList());
    }

    public int getNumberOfActiveUsers() {
        return getListOfActiveUsers().size();
    }
}
