package com.P.G.chatappbackend.controllers;

import com.P.G.chatappbackend.dto.NameRequest;
import com.P.G.chatappbackend.dto.PrivateMoreMessageRequest;
import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.services.PrivateChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class PrivateChatRoomController {

    @Autowired
    private PrivateChatRoomService privateChatRoomService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private Logger logger = Logger.getLogger(PrivateChatRoomController.class.getName());

    @RequestMapping(value = "/create/private-room", method = RequestMethod.GET)
    public @ResponseBody
    String getPrivateRoomId() {
        return privateChatRoomService.createRoom();
    }

    @MessageMapping(value = "/get/name/{roomId}")
    public void getName(@DestinationVariable(value = "roomId") String roomId, @Header("simpSessionId") String sessionId) {
        logger.log(Level.INFO, String.format("Client with sessionId:%s has just made a request for a name in room %s", sessionId, roomId));
        simpMessagingTemplate.convertAndSend(String.format("/queue/%s/%s", roomId, sessionId), new NameRequest(privateChatRoomService.getName(sessionId, roomId)));
    }

    @MessageMapping(value = "/send/{roomId}")
    @SendTo(value = "/topic/{roomId}")
    public Message processMessage(@Payload Message message, @DestinationVariable String roomId, @Header("simpSessionId") String sessionId) {
        logger.log(Level.INFO, String.format("%s has just sent the message %s", message.getSender(), message.getContent()));
        return privateChatRoomService.processMessage(message, roomId);
    }

    @MessageMapping(value = "/update/session/{roomId}")
    public void updateSession(@Payload String name, @DestinationVariable String roomId, @Header("simpSessionId") String sessionId) {
        logger.log(Level.INFO, String.format("Client with sessionId:%s has just made a request to update their session", sessionId));
        privateChatRoomService.updateSessionId(name, sessionId, roomId);
    }

    @MessageMapping(value = "/get/active-users/{roomId}")
    @SendTo(value = "/topic/{roomId}/active-users")
    public List<String> getActiveUsers(@DestinationVariable String roomId, @Header("simpSessionId") String sessionId) {
        logger.log(Level.INFO, String.format("Client with sessionId: %s made a request for active users in room with id: %s", sessionId, roomId));
        return privateChatRoomService.getActiveUsers(roomId);
    }

    @MessageMapping(value = "/get/latest-messages/{roomId}")
    @SendTo(value = "/topic/{roomId}/latest-messages")
    public List<Message> getLatestMessages(@DestinationVariable String roomId, @Header("simpSessionId") String sessionId) {
        logger.log(Level.INFO, String.format("Client with sessionId: %s made a request for active users in room with id: %s", sessionId, roomId));
        return privateChatRoomService.getLatest10Messages(roomId);
    }

    @MessageMapping(value = "/get/previous-messages/{roomId}")
    @SendTo(value = "/topic/{roomId}/previous-messages")
    public List<Message> getPreviousMessages(@Payload PrivateMoreMessageRequest privateMoreMessageRequest, @DestinationVariable String roomId, @Header("simpSessionId") String sessionId) {
        logger.log(Level.INFO, String.format("Client with sessionId: %s made a request for active users in room with id: %s", sessionId, roomId));
        return privateChatRoomService.getPrevious10Messages(privateMoreMessageRequest, roomId);
    }

}
