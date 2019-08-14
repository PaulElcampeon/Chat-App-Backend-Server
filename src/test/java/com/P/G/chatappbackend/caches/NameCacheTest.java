package com.P.G.chatappbackend.caches;

import com.P.G.chatappbackend.cache.NameCache;
import com.P.G.chatappbackend.util.NameCreator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class NameCacheTest {

    @InjectMocks
    private NameCache nameCache;

    @InjectMocks
    private NameCreator nameCreator;

    @Before
    public void initNameCache() {
        nameCache.setNameCache(nameCreator.createNamesConcurrentHashMap());
    }

    @After
    public void tearDown() {
        nameCache.clear();
    }

    @Test
    public void getNameForClient_Test() {
        String name = nameCache.getNameForClient("test123");

        assertEquals("Value should be test123","test123", nameCache.getNames().get(name));
    }

    @Test
    public void freeUpName_Test() {
        String name = nameCache.getNameForClient("test123");

        assertEquals("Value should be test123","test123", nameCache.getNames().get(name));

        nameCache.freeUpName("test123");

        assertEquals("Value should be empty", "", nameCache.getNames().get(name));
    }

    @Test
    public void getListOfActiveUsers_Test() {
        String name1 = nameCache.getNameForClient("test1");
        String name2 = nameCache.getNameForClient("test2");
        String name3 = nameCache.getNameForClient("test3");

        List<String> result = nameCache.getListOfActiveUsers();

        assertEquals(Arrays.asList(name1, name2, name3), result);
    }

    @Test
    public void getNumberOfFreeNames_Test() {
        nameCache.getNameForClient("test1");
        nameCache.getNameForClient("test2");
        nameCache.getNameForClient("test3");

        long result = nameCache.getNumberOfFreeNames();

        assertEquals(122, result);
    }
}
