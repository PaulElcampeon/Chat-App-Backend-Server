package com.P.G.chatappbackend.cache;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component()
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class NameCache {

    private ConcurrentHashMap<String, String> names;

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
            }
        });

    }

    public List<String> getListOfActiveUsers() {
        return names.keySet().stream().filter(key -> names.get(key).length() > 1).collect(Collectors.toList());
    }
}
