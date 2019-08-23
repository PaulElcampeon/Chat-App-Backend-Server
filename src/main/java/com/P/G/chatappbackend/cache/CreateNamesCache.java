package com.P.G.chatappbackend.cache;

import com.P.G.chatappbackend.models.NameHolder;
import com.P.G.chatappbackend.repositiories.NameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CreateNamesCache {

    @Autowired
    private NameRepository nameRepository;

    private HashMap<String, Boolean> names;

    private Logger logger = Logger.getLogger(CreateNamesCache.class.getName());

    private List<String> listOfNames = new ArrayList<>();

    private int size = 0;

    public void setNameCache(HashMap<String, Boolean> hashMap) {
        this.names = hashMap;
        this.listOfNames = new ArrayList<>(names.keySet());
        this.size = names.size();
    }

    public String getNameForClient() {
        synchronized (this) {
            int counter = 0;
            while (true) {
                String name = listOfNames.get(ThreadLocalRandom.current().nextInt(this.size));
                if (names.get(name) && !checkIfNameAlreadyUsed(name)) {
                    names.put(name, false);
                    logger.log(Level.INFO, String.format("Current status of name cache%nAvailable names: %d ", getNumberOfAvailableNames()));
                    addNameToRepository(name);
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

    public void clear() {
        if (names != null) {
            names.clear();
        }
    }

    public HashMap<String, Boolean> getNames() {
        return names;
    }

    public List<String> getListOfNames() {
        return this.listOfNames;
    }

    public int getSize() {
        return size;
    }

    public long getNumberOfAvailableNames() {
        return names.values().stream().filter(name -> name).count();
    }

    public boolean checkIfNameAlreadyUsed(String name) {
        return nameRepository.existsById(name);
    }

    public void addNameToRepository(String name) {
        nameRepository.insert(new NameHolder(name));
    }
}
