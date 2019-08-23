package com.P.G.chatappbackend.controllers;

import com.P.G.chatappbackend.dto.FirstMessagesResponse;
import com.P.G.chatappbackend.dto.GetRoomKeyResponse;
import com.P.G.chatappbackend.dto.PreviousMessagesResponse;
import com.P.G.chatappbackend.models.Mail;
import com.P.G.chatappbackend.models.MailId;
import com.P.G.chatappbackend.services.RoomService;
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
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private Logger logger = Logger.getLogger(RoomController.class.getName());

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String homePage() {
        return "index.html";
    }

    @RequestMapping(value = "/get/room-key", method = RequestMethod.GET)
    @ResponseBody
    GetRoomKeyResponse getRoomKey() {
        logger.log(Level.INFO, String.format("Client has made a request for a room key"));
        return roomService.getRoomKey();
    }

    @RequestMapping(value = "/messages/latest/{roomId}/{numberOfMessages}", method = RequestMethod.GET)
    @ResponseBody
    FirstMessagesResponse getLatestNMessages(@PathVariable("numberOfMessages") int numberOfMessages, @PathVariable("roomId") String roomId) {
        logger.log(Level.INFO, String.format("Client has just made a request for the latest %d messages for room with id: %s", numberOfMessages, roomId));
        return roomService.getLatestNMessages(roomId, numberOfMessages);
    }

    @RequestMapping(value = "/message/previous/{numberOfMessages}", method = RequestMethod.POST)
    @ResponseBody
    public PreviousMessagesResponse getPreviousMessages(@PathVariable("numberOfMessages") int numberOfMessages, @RequestBody MailId mailId) {
        logger.log(Level.INFO, String.format("Request for previous messages was made with mailId %s", mailId));
        ObjectId objectId = new ObjectId(mailId.getTimestamp(), mailId.getMachineIdentifier(), mailId.getProcessIdentifier(), mailId.getCounter());
        return roomService.getNPreviousMessages(objectId, mailId.getRoomId(),numberOfMessages);
    }

    @MessageMapping(value = "/send/{roomId}")
    @SendTo(value = "/topic/room/{roomId}")
    public Mail sendMessage(@DestinationVariable("roomId") String roomId, @RequestBody Mail mail, @Header("simpSessionId") String sessionId) {
        System.out.println(roomId);
        logger.log(Level.INFO, String.format("%s has just sent the mail %s", mail.getSender(), mail.getContent()));
        return roomService.processMessage(mail);
    }
}
