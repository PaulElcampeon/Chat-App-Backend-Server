package com.P.G.chatappbackend.util;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class NameCreator {

    private final String[] adjectives = {"Funny", "Slow", "Dark", "Spotty", "Kind", "Jumping", "Raving", "Colliding", "Heavy", "Lovely"};
    private final String[] nouns = {"Cat", "Dog", "Car", "Puppy", "Raccoon", "Apple", "Banana", "Orange", "Strawberry", "Grape"};
    private final String[] verbs = {"Dripping", "Running", "Swimming", "Farting", "Vomiting", "Wrestling", "Kicking", "Punching", "Shutting", "Driving"};
    private final StringBuilder stringBuilder = new StringBuilder();

    public HashMap<String, Boolean> createMapOfNamesWithAvailability() {
        HashMap<String, Boolean> tempStorage = new HashMap<>(adjectives.length * nouns.length * verbs.length);
        for (String adjective : adjectives) {
            for (String noun : nouns) {
                for (String verb : verbs) {
                    stringBuilder.delete(0, stringBuilder.length());
                    tempStorage.put(stringBuilder.append(adjective).append(noun).append(verb).toString(), true);
                }
            }
        }
        return tempStorage;
    }

//    public List<String> createNamesList() {
//        List<String> tempStorage = new ArrayList<>(adjectives.length * nouns.length * verbs.length);
//        for (String adjective : adjectives) {
//            for (String noun : nouns) {
//                for (String verb : verbs) {
//                    stringBuilder.delete(0, stringBuilder.length());
//                    tempStorage.add(stringBuilder.append(adjective).append(noun).append(verb).toString());
//                }
//            }
//        }
//        return tempStorage;
//    }
}
