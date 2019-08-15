package com.P.G.chatappbackend.controllers;

import com.P.G.chatappbackend.dto.ActiveUsersResponse;
import com.P.G.chatappbackend.dto.FirstMessagesResponse;
import com.P.G.chatappbackend.dto.PreviousMessagesResponse;
import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.models.MessageId;
import com.P.G.chatappbackend.services.PublicChatRoomService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/messages/delete", method = RequestMethod.DELETE)
    public void deleteMessages() {
        chatroomServicePublic.deleteAllMessages();
    }


    @RequestMapping(value = "/messages/latest/{numberOfMessages}", method = RequestMethod.GET)
    @ResponseBody
    FirstMessagesResponse getFirst10Messages(@PathVariable("numberOfMessages") int numberOfMessages) {
        return chatroomServicePublic.getFirstNMessages(numberOfMessages);
    }

    @RequestMapping(value = "/active-users", method = RequestMethod.GET)
    @ResponseBody
    public ActiveUsersResponse getActiveUsers() {
        return chatroomServicePublic.getListOfCurrentUsers();
    }

    @RequestMapping(value = "/active-users/count", method = RequestMethod.GET)
    @ResponseBody
    public int getNumberOfActiveUsers() {
        return chatroomServicePublic.getNumberOfCurrentUsers();
    }

    @RequestMapping(value = "/test/message/get/{messagePos}", method = RequestMethod.GET)
    @ResponseBody
    public Message getMessageTest(@PathVariable("messagePos") int messagePos) {
        return chatroomServicePublic.test(messagePos);
    }

    @RequestMapping(value = "/test/message/send", method = RequestMethod.POST)
    @ResponseBody
    public void createMessageTest(@RequestBody Message message) {
        chatroomServicePublic.processMessage(message);
    }


    @RequestMapping(value = "/message/previous/{numberOfMessages}", method = RequestMethod.POST)
    @ResponseBody
    public PreviousMessagesResponse getPreviousMessages(@PathVariable("numberOfMessages") int numberOfMessages, @RequestBody MessageId messageId) {
        logger.log(Level.INFO, String.format("Message id is %s", messageId));
        ObjectId objectId = new ObjectId(messageId.getTimestamp(), messageId.getMachineIdentifier(), messageId.getProcessIdentifier(), messageId.getCounter());
        logger.log(Level.INFO, String.format("Object id is %s", objectId));
        return chatroomServicePublic.getNPreviousMessages(objectId, numberOfMessages);
    }

    @MessageMapping(value = "/send")
    @SendTo(value = "/topic/public-room")
    public Message sendMessage(@RequestBody Message message, @Header("simpSessionId") String sessionId) {
        logger.log(Level.INFO, String.format("%s has just sent the message %s", message.getSender(), message.getContent()));
        return chatroomServicePublic.processMessage(message);
    }

    @MessageMapping(value = "/previous-messages/{numberOfMessages}")
    public void getPreviousMessages(@DestinationVariable("numberOfMessages") int numberOfMessages, @RequestBody MessageId messageId, @Header("simpSessionId") String sessionId) {
        logger.log(Level.INFO, String.format("User with session id:%s made a request for more previous messages", sessionId));
        ObjectId objectId = new ObjectId(messageId.getTimestamp(), messageId.getMachineIdentifier(), messageId.getProcessIdentifier(), messageId.getCounter());
        simpMessagingTemplate.convertAndSend("/queue/" + sessionId, chatroomServicePublic.getNPreviousMessages(objectId, numberOfMessages));
    }

    @MessageMapping(value = "/active-users")
    @SendTo(value = "/topic/public-room/active-users")
    public ActiveUsersResponse getActiveUsers(@Header("simpSessionId") String sessionId) {
        logger.log(Level.INFO, String.format("Client with sessionId:%s has just made a request for list of active users", sessionId));
        return chatroomServicePublic.getListOfCurrentUsers();
    }
}
