package com.P.G.chatappbackend.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class NameCache {

    private ConcurrentHashMap<String, String> names;

    private Logger logger = Logger.getLogger(NameCache.class.getName());

    public ConcurrentHashMap<String, String> getNames() {
        return names;
    }

    private List<String> listOfNames = new ArrayList<>();

    private int size = 0;

    public void setNameCache(ConcurrentHashMap<String, String> concurrentHashMap) {
        this.names = concurrentHashMap;
        this.listOfNames = Collections.list(names.keys());
        this.size = names.size();
    }

    public String getNameForClient(String sessionId) {
        synchronized (this) {
                int counter = 0;
                while (true) {
                    String name = listOfNames.get(ThreadLocalRandom.current().nextInt(this.size));
                    if (names.get(name).equals("")) {
                        names.put(name, sessionId);
                        logger.log(Level.INFO, String.format("Current status of name cache%nAvailable names: %d ", getNumberOfFreeNames()));
                        return name;
                    }
                    counter++;
                    if (counter == this.size) {
                        break;
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
        logger.log(Level.INFO, String.format("Current status of name cache%nAvailable names: %d ", getNumberOfFreeNames()));
    }

    public List<String> getListOfActiveUsers() {
        return names.keySet().stream().filter(key -> names.get(key).length() > 1).collect(Collectors.toList());
    }

    public void freeUpAllNames() {
        names.forEach((key, value) -> {
            names.put(key, "");
        });
    }

    public void clear() {
        if (names != null) { names.clear();}
    }

    public int getNumberOfActiveUsers() {
        return getListOfActiveUsers().size();
    }

    public long getNumberOfFreeNames() {
        return names.values().stream().filter(name -> name.equals("")).count();
    }

}
