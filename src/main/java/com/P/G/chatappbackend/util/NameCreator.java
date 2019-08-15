package com.P.G.chatappbackend.util;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class NameCreator {

    private final String[] adjectives = {"Funny", "Slow", "Dark", "Pasty", "Spotty", "Limping"};
    private final String[] nouns = {"Cat", "Dog", "Car", "Puppy", "Toilet", "Raccoon"};
    private final String[] verbs = {"Towing", "Running", "Swimming", "Farting", "Vomiting", "Fapping"};
    private final StringBuilder stringBuilder = new StringBuilder();

    public ConcurrentHashMap<String, String> createNamesConcurrentHashMap() {
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

    public List<String> createNamesList() {
        List<String> tempStorage = new ArrayList<>(adjectives.length * nouns.length * verbs.length);
        for (String adjective : adjectives) {
            for (String noun : nouns) {
                for (String verb : verbs) {
                    stringBuilder.delete(0, stringBuilder.length());
                    tempStorage.add(stringBuilder.append(adjective).append(noun).append(verb).toString());
                }
            }
        }
        return tempStorage;
    }
}
