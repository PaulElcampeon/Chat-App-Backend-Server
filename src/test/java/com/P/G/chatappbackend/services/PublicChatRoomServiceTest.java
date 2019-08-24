package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.cache.CreatedNamesCache;
import com.P.G.chatappbackend.cache.OnlineUserNameCache;
import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.repositiories.MessageRepository;
import com.P.G.chatappbackend.util.NameCreator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PublicChatRoomServiceTest {

    @Spy
    private CreatedNamesCache createdNamesCache;

    @Spy
    private NameCreator nameCreator;

    @Spy
    private OnlineUserNameCache onlineUserNameCache;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Spy
    private MessageRepository messageRepository;

    @InjectMocks
    private PublicChatRoomServiceImpl chatRoomService;


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        createdNamesCache.clear();
        onlineUserNameCache.clearNames();
    }
    
    @Test
    public void processMessage_Test() {
        String messageContent = "hello";
        Message message = new Message("Dave", messageContent);

        chatRoomService.processMessage(message);

        assertNotEquals("The content of the message should have been encrypted", messageContent, message.getContent());

        verify(messageRepository, times(1)).insert(Mockito.any(Message.class));
    }
}
