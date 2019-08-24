package com.P.G.chatappbackend.controllers;

import com.P.G.chatappbackend.dto.FirstMessagesResponse;
import com.P.G.chatappbackend.dto.PreviousMessagesResponse;
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

import java.util.logging.Level;
import java.util.logging.Logger;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class PublicChatRoomController {

    @Autowired
    private PublicChatRoomService chatRoomServicePublic;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private Logger logger = Logger.getLogger(PublicChatRoomController.class.getName());

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String homePage() {
        return "index.html";
    }

    @RequestMapping(value = "/messages/latest/{numberOfMessages}", method = RequestMethod.GET)
    @ResponseBody
    FirstMessagesResponse getFirst10Messages(@PathVariable("numberOfMessages") int numberOfMessages) {
        logger.log(Level.INFO, String.format("Client has just made a request for the latest %d messages", numberOfMessages));

        return chatRoomServicePublic.getFirstNMessages(numberOfMessages);
    }

    @RequestMapping(value = "/message/previous/{numberOfMessages}", method = RequestMethod.POST)
    @ResponseBody
    public PreviousMessagesResponse getPreviousMessages(@PathVariable("numberOfMessages") int numberOfMessages, @RequestBody MessageId messageId) {
        logger.log(Level.INFO, String.format("Client made a request for previous messages with messageId %s", messageId));

        ObjectId objectId = new ObjectId(messageId.getTimestamp(), messageId.getMachineIdentifier(), messageId.getProcessIdentifier(), messageId.getCounter());

        return chatRoomServicePublic.getNPreviousMessages(objectId, numberOfMessages);
    }

    @MessageMapping(value = "/send")
    @SendTo(value = "/topic/public-room")
    public Message sendMessage(@RequestBody Message message, @Header("simpSessionId") String sessionId) {
        logger.log(Level.INFO, String.format("%s has just sent the message %s", message.getSender(), message.getContent()));

        Message encryptedMessage = chatRoomServicePublic.processMessage(message);

        return chatRoomServicePublic.decryptMessage(encryptedMessage);
    }
}
