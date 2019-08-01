package com.P.G.chatappbackend.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.services.ChatRoomService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ChatroomController {

    @Autowired
    private ChatRoomService chatroomService;

    private Logger logger = Logger.getLogger(ChatroomController.class.getName());

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody
    String homePage() {
        return "Im awake now";
    }

    @MessageMapping(value = "/send")
    @SendTo(value = "/topic/public-room")
    public Message sendMessage(@RequestBody Message message) {
        logger.log(Level.INFO, String.format("%s has just sent the message %s", message.getSender(), message.getContent()));
        chatroomService.processMessage(message);
        return message;
    }
}
