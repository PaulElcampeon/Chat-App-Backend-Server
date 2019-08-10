package com.P.G.chatappbackend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.P.G.chatappbackend.config.WebSocketConfig;
import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.dto.PublicMoreMessagesRequest;
import com.P.G.chatappbackend.repositiories.MessageRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ChatAppBackendApplication.class, WebSocketConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublicChatRoomControllerTest {

    @LocalServerPort
    private int port;

    private CompletableFuture<Message> completableFuture1 = new CompletableFuture<>();
    private CompletableFuture<List<Message>> completableFuture2 = new CompletableFuture<>();
    private CompletableFuture<String> completableFuture3 = new CompletableFuture<>();

    @Autowired
    private MessageRepository messageRepository;

    @Before
    public void tearDown() {
        messageRepository.deleteAll();
    }

    @Test
    public void sendMessage_Test() throws InterruptedException, ExecutionException, TimeoutException {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSession stompSession = stompClient.connect(String.format("ws://localhost:%d/ima", port), new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);
        stompSession.subscribe("/topic/public-room", new sendMessageFrameHandler());
        stompSession.send("/app/send", new Message("Dave", "Hello"));
        Message message = completableFuture1.get(10, TimeUnit.SECONDS);
        assertNotNull(message);
        stompSession.disconnect();
    }

    @Test
    public void getPreviousMessages_Test() throws InterruptedException, ExecutionException, TimeoutException {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSession stompSession = stompClient.connect(String.format("ws://localhost:%d/ima", port), new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);

        stompSession.subscribe("/topic/public-room", new sendMessageFrameHandler());
        stompSession.send("/app/send", new Message("Dave", "Hello"));

        Message message1 = new Message("Sanji", "Hello alll");
        Message message2 = new Message("Kable", "Bye");
        Message message3 = new Message("Nevo", "Lol");
        Message message4 = new Message("Mable", "Podi");

        messageRepository.saveAll(Arrays.asList(message1, message2, message3, message4));
        stompSession.subscribe("/queue/"+completableFuture3.get(10, TimeUnit.SECONDS), new getPreviousMessagesFrameHandler());
        stompSession.send("/app/previous-messages", new PublicMoreMessagesRequest(message3.get_id()));
        List<Message> messages = completableFuture2.get(10, TimeUnit.SECONDS);
        assertEquals(messageRepository.findFirst10By_idLessThan(message3.get_id()).size(), messages.size());
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
            return List.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            completableFuture2.complete((List<Message>) o);
        }
    }
}
