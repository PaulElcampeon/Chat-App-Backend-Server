package com.P.G.chatappbackend.utils;

import com.P.G.chatappbackend.cache.CreateNamesCache;
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
    private CreateNamesCache nameCache;

    @InjectMocks
    private NameCreator nameCreator;

    @Test
    public void nameCreator_Test() {
        assertEquals("Number of distinct names created should be 3375", 3375, nameCreator.createMapOfNamesWithAvailability().size());
    }
}
