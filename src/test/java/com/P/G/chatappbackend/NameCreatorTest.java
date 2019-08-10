package com.P.G.chatappbackend;

import com.P.G.chatappbackend.cache.NameCache;
import com.P.G.chatappbackend.util.NameCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class NameCreatorTest {

    @Spy
    private NameCache nameCache;

    @InjectMocks
    private NameCreator nameCreator;

    @Test
    public void nameCreator_Test() {
        assertEquals("Number of distinct names created should be 125", 125, nameCreator.createNamesConcurrentHashMap().mappingCount());
    }
}
