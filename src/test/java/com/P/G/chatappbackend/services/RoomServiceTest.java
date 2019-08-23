package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.cache.CreateNamesCache;
import com.P.G.chatappbackend.cache.OnlineUserNameCache;
import com.P.G.chatappbackend.models.Mail;
import com.P.G.chatappbackend.repositiories.MailRepository;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RoomServiceTest {

    @Spy
    private CreateNamesCache nameCache;

    @Spy
    private NameCreator nameCreator;

    @Spy
    private OnlineUserNameCache onlineUserNameCache;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private MailRepository messageRepository;

    @InjectMocks
    private RoomServiceImpl roomService;

    @Before
    public void init() {
        roomService.initializeNameCache();
    }

    @After
    public void tearDown() {
        nameCache.clear();
        onlineUserNameCache.clearNames();
    }

    @Test
    public void initializeNameCache_Test() {
        assertEquals("Number of names should be 3375", 3375, nameCache.getNames().size());

        verify(nameCreator, times(1)).createMapOfNamesWithAvailability();
        verify(nameCache, times(1)).setNameCache(Mockito.any());
    }

    @Test
    public void processMessage_Test() {
        String messageContent = "hello";
        Mail message = new Mail("Dave", messageContent);

        roomService.processMessage(message);

        assertEquals("The content of the message should be equal", "hello", message.getContent());
    }

    @Test
    public void encryptMessage() {
        String messageContent = "hello";
        Mail message = new Mail("Dave", messageContent);

        roomService.encryptMessage(message);

        assertNotEquals("The content of the message should be equal", "hello", message.getContent());
    }

    @Test
    public void decryptMessage() {
        String messageContent = "hello";
        Mail message = new Mail("Dave", messageContent);

        roomService.encryptMessage(message);

        assertNotEquals("The content of the message should not be equal", messageContent, message.getContent());

        roomService.decryptMessage(message);

        assertEquals("The content of the message should be equal", "hello", message.getContent());
    }
}
