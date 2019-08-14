package com.P.G.chatappbackend.controllers;

import com.P.G.chatappbackend.ChatAppBackendApplication;
import com.P.G.chatappbackend.config.WebSocketConfig;
import com.P.G.chatappbackend.dto.NameRequest;
import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.repositiories.PrivateChatRoomRepository;
import com.P.G.chatappbackend.services.PrivateChatRoomService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ChatAppBackendApplication.class, WebSocketConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PrivateChatRoomControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PrivateChatRoomRepository privateChatRoomRepository;

    @Autowired
    private PrivateChatRoomService privateChatRoomService;

    private CompletableFuture<NameRequest> completableFuture = new CompletableFuture<>();

    private CompletableFuture<String> completableFutureSessionId = new CompletableFuture<>();

    private CompletableFuture<Message> completableFutureMessage = new CompletableFuture<>();

    @Before
    public void tearDown() {
        privateChatRoomRepository.deleteAll();
    }

    @Test
    public void getPrivateRoomId_Test() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/create/private-room", String.class);

        String roomId = responseEntity.getBody();

        assertTrue(privateChatRoomRepository.existsById(roomId));
        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void getName_Test() throws InterruptedException, ExecutionException, TimeoutException {
        String roomId = privateChatRoomService.createRoom();

        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(String.format("ws://localhost:%d/ima", port), new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                completableFutureSessionId.complete(connectedHeaders.get("sessionId").get(0));
            }
        }).get(1, SECONDS);

        completableFutureSessionId.thenRun(() -> {
            try {
                stompSession.subscribe(String.format("/queue/%s/%s", roomId, completableFutureSessionId.get()), new RequestMessageFrameHandler());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            stompSession.send("/app/get/name/" + roomId, "");
            NameRequest name = null;
            try {
                name = completableFuture.get(10, SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }

            assertNotNull(name);

            stompSession.disconnect();
        });
    }

    @Test
    public void processMessage_Test() throws InterruptedException, ExecutionException, TimeoutException {
        String roomId = privateChatRoomService.createRoom();

        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(String.format("ws://localhost:%d/ima", port), new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                completableFutureSessionId.complete(connectedHeaders.get("sessionId").get(0));
            }
        }).get(1, SECONDS);

        completableFutureSessionId.thenRun(() -> {
            stompSession.subscribe(String.format("/topic/%s", roomId), new SendMessageFrameHandler());
            stompSession.send("/app/send/" + roomId, new Message("Kimberly", "red"));
            Message message = null;
            try {
                message = completableFutureMessage.get(10, SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }

            assertNotNull(message);

            stompSession.disconnect();
        });
    }

    private class RequestMessageFrameHandler extends StompSessionHandlerAdapter {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return NameRequest.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            //substring due to the way we receive the messageId we need to remove the last 2 characters from it to match the sessionId
            completableFuture.complete((NameRequest) o);
        }
    }

    private class SendMessageFrameHandler extends StompSessionHandlerAdapter {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return Message.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            //substring due to the way we receive the messageId we need to remove the last 2 characters from it to match the sessionId
            completableFutureMessage.complete((Message) o);
        }
    }
}
