package com.P.G.chatappbackend.caches;

import com.P.G.chatappbackend.cache.CreatedNamesCache;
import com.P.G.chatappbackend.models.NameHolder;
import com.P.G.chatappbackend.repositiories.NameRepository;
import com.P.G.chatappbackend.util.NameCreator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreatedNamesCacheTest {


    @Spy
    private NameCreator nameCreator;

    @Mock
    private NameRepository nameRepository;

    @InjectMocks
    private CreatedNamesCache nameCache;

    @Before
    public void init() {
        nameCache.setNameCache(nameCreator.createMapOfNamesWithAvailability());
    }

    @After
    public void tearDown() {
        nameCache.clear();
    }

    @Test
    public void setNameCache_Test() {
        //nameCache already set before each test
        nameCache.getListOfNames();
        nameCache.getSize();
        nameCache.getNames();

        assertNotNull(nameCache.getListOfNames());
        assertEquals(nameCache.getListOfNames().size(), nameCache.getSize());
        assertNotNull(nameCache.getNames());
    }

    @Test
    public void getNameForClient_Test() {
        when(nameRepository.existsById(Mockito.anyString())).thenReturn(false);

        String name = nameCache.getNameForClient();

        assertFalse(nameCache.getNames().get(name));
        assertNotNull(name);
        verify(nameRepository, times(1)).insert(Mockito.any(NameHolder.class));
    }

    @Test
    public void getNumberOfAvailableNames_Test() {
        int noOfCreatedUsers = 5;
        for (int i = 0; i < noOfCreatedUsers; i++) {
            nameCache.getNameForClient();
        }

        long result = nameCache.getNumberOfAvailableNames();

        assertEquals(nameCache.getSize() - noOfCreatedUsers, result);
    }
}
