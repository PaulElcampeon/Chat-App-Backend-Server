package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.cache.CreateNamesCache;
import com.P.G.chatappbackend.cache.OnlineUserNameCache;
import com.P.G.chatappbackend.dto.OnlineUsers;
import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.repositiories.MessageRepository;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PublicChatRoomServiceTest {

    @Spy
    private CreateNamesCache nameCache;

    @Spy
    private NameCreator nameCreator;

    @Spy
    private OnlineUserNameCache onlineUserNameCache;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private PublicChatRoomServiceImpl chatRoomService;

    @Before
    public void init() {
        chatRoomService.initializeNameCache();
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
        Message message = new Message("Dave", messageContent);

        chatRoomService.processMessage(message);

        assertNotEquals("The content of the message should have been encrypted", messageContent, message.getContent());
    }
}
