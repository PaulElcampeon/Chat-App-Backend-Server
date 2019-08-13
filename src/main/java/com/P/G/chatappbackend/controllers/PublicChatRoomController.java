package com.P.G.chatappbackend.controllers;

import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.models.MessageId;
import com.P.G.chatappbackend.services.PublicChatRoomService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class PublicChatRoomController {

    @Autowired
    private PublicChatRoomService chatroomServicePublic;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private Logger logger = Logger.getLogger(PublicChatRoomController.class.getName());

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody
    String homePage() {
        return "Im awake now";
    }

    @RequestMapping(value = "/messages/latest/10", method = RequestMethod.GET)
    @ResponseBody
    List<Message> getFirst10Messages() {
        return chatroomServicePublic.getFirst10Messages();
    }

    @RequestMapping(value = "/active-users", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getActiveUsers() {
        return chatroomServicePublic.getListOfCurrentUsers();
    }

    @RequestMapping(value = "/active-users/count", method = RequestMethod.GET)
    @ResponseBody
    public int getNumberOfActiveUsers() {
        return chatroomServicePublic.getNumberOfCurrentUsers();
    }

    @RequestMapping(value = "/message/previous/10", method = RequestMethod.POST)
    @ResponseBody
    public List<Message> getPreviousMessages(@RequestBody MessageId messageId) {
        ObjectId objectId = new ObjectId(messageId.getTimestamp(), messageId.getMachineIdentifier(), messageId.getProcessIdentifier(), messageId.getCounter());
        return chatroomServicePublic.getPrevious10Messages(objectId);
    }

    @MessageMapping(value = "/send")
    @SendTo(value = "/topic/public-room")
    public Message sendMessage(@RequestBody Message message, @Header("simpSessionId") String sessionId) {
        logger.log(Level.INFO, String.format("%s has just sent the message %s", message.getSender(), message.getContent()));
        return chatroomServicePublic.processMessage(message);
    }

    @MessageMapping(value = "/previous-messages")
    public void getPreviousMessages(@RequestBody MessageId messageId, @Header("simpSessionId") String sessionId) {
        logger.log(Level.INFO, String.format("User with session id:%s made a request for more previous messages", sessionId));
        ObjectId objectId = new ObjectId(messageId.getTimestamp(), messageId.getMachineIdentifier(), messageId.getProcessIdentifier(), messageId.getCounter());
        simpMessagingTemplate.convertAndSend("/queue/" + sessionId, chatroomServicePublic.getPrevious10Messages(objectId));
    }

    @MessageMapping(value = "/active-users")
    @SendTo(value = "/topic/public-room/active-users")
    public List<String> getActiveUsers(@Header("simpSessionId") String sessionId) {
        logger.log(Level.INFO, String.format("Client with sessionId:%s has just made a request for list of active users", sessionId));
        return chatroomServicePublic.getListOfCurrentUsers();
    }
}
