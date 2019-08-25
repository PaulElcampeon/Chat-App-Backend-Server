package com.P.G.chatappbackend.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class WebSocketChatEventListener {

    @Autowired
    @Lazy
    private SimpMessagingTemplate messagingTemplate;

    private Logger logger = Logger.getLogger(WebSocketChatEventListener.class.getName());

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.log(Level.INFO, String.format("Received a new web socket connection"));
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println(headerAccessor.getSessionAttributes());
        System.out.println(headerAccessor);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
        }
    }
}
