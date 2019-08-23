package com.P.G.chatappbackend.controllers;

import com.P.G.chatappbackend.ChatAppBackendApplication;
import com.P.G.chatappbackend.NameAndRoomIdHolder;
import com.P.G.chatappbackend.cache.CreateNamesCache;
import com.P.G.chatappbackend.cache.OnlineUserNameCache;
import com.P.G.chatappbackend.config.WebSocketConfig;
import com.P.G.chatappbackend.dto.FirstMessagesResponse;
import com.P.G.chatappbackend.dto.OnlineUsers;
import com.P.G.chatappbackend.dto.PreviousMessagesResponse;
import com.P.G.chatappbackend.models.Mail;
import com.P.G.chatappbackend.models.MailId;
import com.P.G.chatappbackend.repositiories.MailRepository;
import com.P.G.chatappbackend.repositiories.NameRepository;
import com.P.G.chatappbackend.repositiories.RoomRepository;
import com.P.G.chatappbackend.services.RoomService;
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
public class RoomControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MailRepository messageRepository;

    @Autowired
    private NameRepository nameRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CreateNamesCache nameCache;

    @Autowired
    private OnlineUserNameCache onlineUserNameCache;

    @Before
    public void init() {
        nameCache.clear();
        onlineUserNameCache.clearNames();
        roomService.initializeNameCache();
        messageRepository.deleteAll();
        nameRepository.deleteAll();
        roomRepository.deleteAll();
    }

    @Test
    public void getFirstNMessages() {
        Mail message1 = new Mail("Cathy", "hello", "test1");
        Mail message2 = new Mail("Lathy", "mello", "test1");
        Mail message3 = new Mail("Ian", "mello", "test1");

        roomService.processMessage(message1);
        roomService.processMessage(message2);
        roomService.processMessage(message3);

        ResponseEntity<FirstMessagesResponse> response = restTemplate.getForEntity("http://localhost:" + port + "/messages/latest/test1/2", FirstMessagesResponse.class);

        assertEquals(Arrays.asList(message3, message2), response.getBody().getMessages());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getPreviousNMessages() {
        Mail message1 = new Mail("Cathy", "hello", "test1");
        Mail message2 = new Mail("Lathy", "mello", "test1");
        Mail message3 = new Mail("Ian", "kinda", "test1");
        Mail message4 = new Mail("Dav", "pluck", "test1");
        Mail message5 = new Mail("Fred", "Shut it", "test1");

        roomService.processMessage(message1);
        roomService.processMessage(message2);
        roomService.processMessage(message3);
        roomService.processMessage(message4);
        roomService.processMessage(message5);

        MailId mailId = new MailId();
        mailId.setRoomId("test1");
        mailId.setCounter(message5.getId().getCounter());
        mailId.setMachineIdentifier(message5.getId().getMachineIdentifier());
        mailId.setProcessIdentifier(message5.getId().getProcessIdentifier());
        mailId.setTimestamp(message5.getId().getTimestamp());

        ResponseEntity<PreviousMessagesResponse> response = restTemplate.postForEntity("http://localhost:" + port + "/message/previous/3", mailId, PreviousMessagesResponse.class);

        assertEquals(Arrays.asList(message4, message3, message2), response.getBody().getMessages());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void sendMessage() throws InterruptedException, ExecutionException, TimeoutException {
        //TODO test for name in header, added username in header
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(String.format("ws://localhost:%d/strawberryCR", port), new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);

        CompletableFuture<MessageAndSessionIdHolder> completableFuture = new CompletableFuture<>();

        stompSession.subscribe("/topic/room/test1", new SendMessageFrameHandler(completableFuture));

        stompSession.send("/app/send/test1", new Mail("Dave", "Hello", "test1"));

        MessageAndSessionIdHolder message = completableFuture.get(10, TimeUnit.SECONDS);

        assertEquals("Hello", message.getMessage().getContent());
        assertNotNull(message.getMessage());

        stompSession.disconnect();
    }

    @Data
    private class MessageAndSessionIdHolder {
        private Mail message;
        private String sessionId;
    }

    private class SendMessageFrameHandler implements StompFrameHandler {

        private CompletableFuture<MessageAndSessionIdHolder> completableFuture;

        private SendMessageFrameHandler(CompletableFuture<MessageAndSessionIdHolder> completableFuture) {
            this.completableFuture = completableFuture;
        }

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return Mail.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            MessageAndSessionIdHolder messageAndSessionIdHolder = new MessageAndSessionIdHolder();
            messageAndSessionIdHolder.setSessionId(stompHeaders.getMessageId().substring(0, stompHeaders.getMessageId().length() - 2));
            messageAndSessionIdHolder.setMessage((Mail) o);
            this.completableFuture.complete(messageAndSessionIdHolder);
        }
    }
}
