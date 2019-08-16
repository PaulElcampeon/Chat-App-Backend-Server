package com.P.G.chatappbackend.controllers;

import com.P.G.chatappbackend.ChatAppBackendApplication;
import com.P.G.chatappbackend.cache.CreateNamesCache;
import com.P.G.chatappbackend.cache.OnlineUserNameCache;
import com.P.G.chatappbackend.config.WebSocketConfig;
import com.P.G.chatappbackend.dto.OnlineUsers;
import com.P.G.chatappbackend.dto.FirstMessagesResponse;
import com.P.G.chatappbackend.dto.PreviousMessagesResponse;
import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.repositiories.MessageRepository;
import com.P.G.chatappbackend.services.PublicChatRoomService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ChatAppBackendApplication.class, WebSocketConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublicChatRoomControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PublicChatRoomService publicChatRoomService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CreateNamesCache nameCache;

    @Autowired
    private OnlineUserNameCache onlineUserNameCache;

    private CompletableFuture<Message> completableFuture1 = new CompletableFuture<>();
    private CompletableFuture<PreviousMessagesResponse> completableFuture2 = new CompletableFuture<>();
    private CompletableFuture<String> completableFuture3 = new CompletableFuture<>();

    @Before
    public void init() {
        nameCache.clear();
        messageRepository.deleteAll();
        publicChatRoomService.initializeNameCache();
        onlineUserNameCache.clearNames();
    }

    @Test
    public void homePage_Test() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port, String.class);

        assertEquals("Im awake now", response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getFirstNMessages_Test() {
        Message message1 = new Message("Cathy", "hello");
        Message message2 = new Message("Lathy", "mello");
        Message message3 = new Message("Ian", "mello");

        messageRepository.insert(Arrays.asList(message1, message2, message3));

        ResponseEntity<FirstMessagesResponse> response = restTemplate.getForEntity("http://localhost:" + port + "/messages/latest/2", FirstMessagesResponse.class);

        assertEquals(Arrays.asList(message3, message2), response.getBody().getMessages());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getPreviousNMessages_HttpPost_Test() {
        Message message1 = new Message("Cathy", "hello");
        Message message2 = new Message("Lathy", "mello");
        Message message3 = new Message("Ian", "kinda");
        Message message4 = new Message("Dav", "pluck");
        Message message5 = new Message("Fred", "Shut it");

        messageRepository.insert(Arrays.asList(message1, message2, message3, message4, message5));

        ResponseEntity<PreviousMessagesResponse> response = restTemplate.postForEntity("http://localhost:" + port + "/message/previous/3", message5.get_id(), PreviousMessagesResponse.class);

        assertEquals(Arrays.asList(message4, message3, message2), response.getBody().getMessages());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getActiveUsers_Test() {
        String session1 = "session1";
        String session2 = "session2";

        String name1 = nameCache.getNameForClient();
        String name2 = nameCache.getNameForClient();

        onlineUserNameCache.addNewOnlineUser(name1, session1);
        onlineUserNameCache.addNewOnlineUser(name2, session2);

        ResponseEntity<OnlineUsers> response = restTemplate.getForEntity("http://localhost:" + port + "/active-users", OnlineUsers.class);
        List<String> activeUsers = response.getBody().getUsers();

        assertTrue(activeUsers.containsAll(Arrays.asList(name1, name2)));
    }

    @Test
    public void getNumberOfActiveUsers_Test() {
        String session1 = "session1";
        String session2 = "session2";

        String name1 = nameCache.getNameForClient();
        String name2 = nameCache.getNameForClient();

        onlineUserNameCache.addNewOnlineUser(name1, session1);
        onlineUserNameCache.addNewOnlineUser(name2, session2);

        ResponseEntity<Integer> response = restTemplate.getForEntity("http://localhost:" + port + "/active-users/count", Integer.class);
        int count = response.getBody();

        assertEquals(2, count);
    }

    @Test
    public void sendMessage_Test() throws InterruptedException, ExecutionException, TimeoutException {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(String.format("ws://localhost:%d/ima", port), new StompSessionHandlerAdapter() {
        }).get(1, TimeUnit.SECONDS);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("username", "Dave");

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/topic/public-room");
        stompHeaders.setAll(hashMap);

        stompSession.subscribe(stompHeaders, new sendMessageFrameHandler());

        stompSession.send("/app/send", new Message("Dave", "Hello"));

        Message message = completableFuture1.get(10, TimeUnit.SECONDS);

        assertNotNull(message);

        stompSession.disconnect();
    }

    @Test
    public void getPreviousNMessages_Websocket_Test() throws InterruptedException, ExecutionException, TimeoutException {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSession stompSession = stompClient.connect(String.format("ws://localhost:%d/ima", port), new StompSessionHandlerAdapter() {
        }).get(1, TimeUnit.SECONDS);

        stompSession.subscribe("/topic/public-room", new sendMessageFrameHandler());
        stompSession.send("/app/send", new Message("Dave", "Hello"));

        Message message1 = new Message("Sanji", "Hello alll");
        Message message2 = new Message("Kable", "Bye");
        Message message3 = new Message("Nevo", "Lol");
        Message message4 = new Message("Mable", "Podi");

        messageRepository.saveAll(Arrays.asList(message1, message2, message3, message4));

        stompSession.subscribe("/queue/" + completableFuture3.get(10, TimeUnit.SECONDS), new getPreviousMessagesFrameHandler());

        stompSession.send("/app/previous-messages/2", message4.get_id());

        PreviousMessagesResponse results = completableFuture2.get(10, TimeUnit.SECONDS);

        assertEquals(2, results.getMessages().size());
        assertEquals(Arrays.asList(message3, message2), results.getMessages());

        stompSession.disconnect();
    }

    private class sendMessageFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return Message.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            completableFuture3.complete(stompHeaders.getMessageId().substring(0, stompHeaders.getMessageId().length() - 2));
            completableFuture1.complete((Message) o);
        }
    }

    private class getPreviousMessagesFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return PreviousMessagesResponse.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            completableFuture2.complete((PreviousMessagesResponse) o);
        }
    }
}
