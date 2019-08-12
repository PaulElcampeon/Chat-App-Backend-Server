package com.P.G.chatappbackend.services;

import com.P.G.chatappbackend.cache.NameCache;
import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.repositiories.MessageRepository;
import com.P.G.chatappbackend.services.PublicChatRoomServiceImpl;
import com.P.G.chatappbackend.util.NameCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PublicChatPrivateChatRoomServiceTest {

    @Spy
    private NameCache nameCache;

    @Spy
    private NameCreator nameCreator;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private PublicChatRoomServiceImpl chatRoomService;


    @Test
    public void initializeNameCache_Test() {
        chatRoomService.initializeNameCache();
        assertEquals("Number of names should be 125",125, nameCache.getNames().mappingCount());
        verify(nameCreator,times(1)).createNamesConcurrentHashMap();
        verify(nameCache,times(1)).setNameCache(Mockito.any());
    }

    @Test
    public void assignUserRandomName_Test() {
        chatRoomService.initializeNameCache();
        String result = chatRoomService.assignUserRandomName("test123");
        assertNotNull("name should not be null", result);
        verify(nameCache,times(1)).getNameForClient(Mockito.anyString());
    }

    @Test
    public void processMessage_Test() {
        chatRoomService.processMessage(new Message());
//        verify(messageRepository, times(1)).insert(Mockito.any(Message.class));
    }

    @Test
    public void getListOfCurrentUsers_Test() {
        chatRoomService.initializeNameCache();
        chatRoomService.assignUserRandomName("test1");
        chatRoomService.assignUserRandomName("test2");
        chatRoomService.assignUserRandomName("test3");
        List<String> names = chatRoomService.getListOfCurrentUsers();
        assertEquals("Should be 3 names", 3, names.size());
    }

    @Test
    public void freeUpName_Test() {
        chatRoomService.initializeNameCache();
        chatRoomService.assignUserRandomName("test1");
        chatRoomService.freeUpName("test1");
        verify(nameCache, times(1)).freeUpName("test1");
    }
}
