package com.P.G.chatappbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionConnectEvent;
//import org.springframework.web.socket.messaging.SessionConnectedEvent;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketChatEventListener {

//    @Autowired
//    @Lazy
//    private SimpMessagingTemplate messagingTemplate;
//
//    @EventListener
//    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
////        System.out.println("Received a new web socket connection");
////        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
////        System.out.println(headerAccessor.getCommand());
////        headerAccessor.setNativeHeader("username", "Dave" );
////        headerAccessor.setHeader("username", "Dave" );
////
////        System.out.println(headerAccessor);
//
//
//    }
//
//    @EventListener
//    public void handleWebSocketConnectListener(SessionConnectEvent event) {
//        System.out.println("Received a new web socket connection");
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        headerAccessor.setNativeHeader("username", "Dave" );
//        headerAccessor.setHeader("username", "Dave" );
//
//    }
//
//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
////        String username = (String) headerAccessor.getSessionAttributes().get("username");
////        if(username != null) {
////            WebSocketChatMessage chatMessage = new WebSocketChatMessage();
////            chatMessage.setType("Leave");
////            chatMessage.setSender(username);
////            messagingTemplate.convertAndSend("/topic/public", chatMessage);
////        }
//    }
}
