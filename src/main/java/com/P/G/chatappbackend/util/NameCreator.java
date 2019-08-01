package com.P.G.chatappbackend.util;

import com.P.G.chatappbackend.cache.NameCache;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class NameCreator {

    private final String[] adjectives = {"Funny", "Slow", "Dark", "Pasty", "Spotty"};
    private final String[] nouns = {"Cat", "Dog", "Car", "Puppy", "Toilet"};
    private final String[] verbs = {"Towing", "Running", "Swimming", "Farting", "Vomiting"};
    private final StringBuilder stringBuilder = new StringBuilder();

    public ConcurrentHashMap<String, String> createNames() {
        ConcurrentHashMap<String, String> tempStorage = new ConcurrentHashMap<>(adjectives.length * nouns.length * verbs.length);
        for (String adjective : adjectives) {
            for (String noun : nouns) {
                for (String verb : verbs) {
                    stringBuilder.delete(0, stringBuilder.length());
                    tempStorage.put(stringBuilder.append(adjective).append(noun).append(verb).toString(), "");
                }
            }
        }
        return tempStorage;
    }
}
