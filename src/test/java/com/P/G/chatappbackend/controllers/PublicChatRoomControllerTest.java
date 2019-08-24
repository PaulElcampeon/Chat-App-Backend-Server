package com.P.G.chatappbackend.controllers;

import com.P.G.chatappbackend.ChatAppBackendApplication;
import com.P.G.chatappbackend.cache.CreateNamesCache;
import com.P.G.chatappbackend.cache.OnlineUserNameCache;
import com.P.G.chatappbackend.config.WebSocketConfig;
import com.P.G.chatappbackend.dto.FirstMessagesResponse;
import com.P.G.chatappbackend.dto.PreviousMessagesResponse;
import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.repositiories.MessageRepository;
import com.P.G.chatappbackend.services.PublicChatRoomService;
import lombok.Data;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    @Before
    public void init() {
        nameCache.clear();
        messageRepository.deleteAll();
        publicChatRoomService.initializeNameCache();
        onlineUserNameCache.clearNames();
    }

    @Test
    public void getFirstNMessages_Test() {
        Message message1 = new Message("Cathy", "hello");
        Message message2 = new Message("Lathy", "mello");
        Message message3 = new Message("Ian", "mello");

        publicChatRoomService.processMessage(message1);
        publicChatRoomService.processMessage(message2);
        publicChatRoomService.processMessage(message3);

        publicChatRoomService.decryptMessage(message1);
        publicChatRoomService.decryptMessage(message2);
        publicChatRoomService.decryptMessage(message3);

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

        publicChatRoomService.processMessage(message1);
        publicChatRoomService.processMessage(message2);
        publicChatRoomService.processMessage(message3);
        publicChatRoomService.processMessage(message4);
        publicChatRoomService.processMessage(message5);

        publicChatRoomService.decryptMessage(message1);
        publicChatRoomService.decryptMessage(message2);
        publicChatRoomService.decryptMessage(message3);
        publicChatRoomService.decryptMessage(message4);
        publicChatRoomService.decryptMessage(message5);

        ResponseEntity<PreviousMessagesResponse> response = restTemplate.postForEntity("http://localhost:" + port + "/message/previous/3", message5.get_id(), PreviousMessagesResponse.class);

        assertEquals(Arrays.asList(message4, message3, message2), response.getBody().getMessages());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void sendMessage_Test() throws InterruptedException, ExecutionException, TimeoutException {
        //TODO test for name in header, added username in header
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(String.format("ws://localhost:%d/ima", port), new StompSessionHandlerAdapter() {
        }).get(1, TimeUnit.SECONDS);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("username", "Dave");

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/topic/public-room");
        stompHeaders.setAll(hashMap);

        CompletableFuture<MessageAndSessionIdHolder> completableFuture = new CompletableFuture<>();

        stompSession.subscribe(stompHeaders, new SendMessageFrameHandler(completableFuture));

        stompSession.send("/app/send", new Message("Dave", "Hello"));

        MessageAndSessionIdHolder message = completableFuture.get(10, TimeUnit.SECONDS);

        assertNotNull(message.getMessage());

        stompSession.disconnect();
    }

    @Data
    private class MessageAndSessionIdHolder {
        private Message message;
        private String sessionId;
    }

    private class SendMessageFrameHandler implements StompFrameHandler {

        private CompletableFuture<MessageAndSessionIdHolder> completableFuture;

        private SendMessageFrameHandler(CompletableFuture<MessageAndSessionIdHolder> completableFuture) {
            this.completableFuture = completableFuture;
        }

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return Message.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            MessageAndSessionIdHolder messageAndSessionIdHolder = new MessageAndSessionIdHolder();
            messageAndSessionIdHolder.setSessionId(stompHeaders.getMessageId().substring(0, stompHeaders.getMessageId().length() - 2));
            messageAndSessionIdHolder.setMessage((Message) o);
            this.completableFuture.complete(messageAndSessionIdHolder);
        }
    }
}
