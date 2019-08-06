package com.P.G.chatappbackend;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.P.G.chatappbackend.cache.NameCache;
import com.P.G.chatappbackend.config.WebSocketConfig;
import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.repositiories.MessageRepository;
import com.P.G.chatappbackend.services.ChatRoomService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ChatAppBackendApplication.class, WebSocketConfig.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ChatRoomControllerIT {

    @Autowired
    private NameCache nameCache;

    @Autowired
    private MessageRepository messageRepository;


    @LocalServerPort
    private int port;

    private CompletableFuture<String> completableFuture = new CompletableFuture<>();

    @Before
    public void tearDown() {
        messageRepository.deleteAll();
    }

    @Test
    public void checkClientHasBeenAssigned_Test() throws InterruptedException, ExecutionException, TimeoutException {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSession stompSession = stompClient.connect(String.format("ws://localhost:%d/ima", port), new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);
        stompSession.subscribe("/topic/public-room", new sendMessageFrameHandler());
        stompSession.send("/app/send", new Message("Dave", "Hello"));
        String sessionId = completableFuture.get(10, SECONDS);
        assertTrue(nameCache.getNames().containsValue(sessionId));
        stompSession.disconnect();
    }

    private class sendMessageFrameHandler extends StompSessionHandlerAdapter {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return Message.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            //substring due to the way we receive the messageId we need to remove the last 2 characters from it to match the sessionId
            completableFuture.complete(stompHeaders.getMessageId().substring(0, stompHeaders.getMessageId().length() - 2));
        }
    }
}
