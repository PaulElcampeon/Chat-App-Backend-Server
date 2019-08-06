package com.P.G.chatappbackend.controllers;

import com.P.G.chatappbackend.models.Message;
import com.P.G.chatappbackend.models.MoreMessagesRequest;
import com.P.G.chatappbackend.services.ChatRoomService;
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
public class ChatroomController {

    @Autowired
    private ChatRoomService chatroomService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private Logger logger = Logger.getLogger(ChatroomController.class.getName());

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody
    String homePage() {
        return "Im awake now";
    }

    @RequestMapping(value = "/messages/latest/10", method = RequestMethod.GET)
    @ResponseBody
    List<Message> getFirst10Messages() {
        return chatroomService.getFirst10Messages();
    }

    @RequestMapping(value = "/active-users", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getActiveUsers() {
        return chatroomService.getListOfCurrentUsers();
    }

    @RequestMapping(value = "/active-users/count", method = RequestMethod.GET)
    @ResponseBody
    public int getNumberOfActiveUsers() {
        return chatroomService.getNumberOfCurrentUsers();
    }

    @RequestMapping(value = "/message/previous/10", method = RequestMethod.POST)
    @ResponseBody
    public List<Message> getPreviousMessages(@RequestBody MoreMessagesRequest moreMessagesRequest) {
        return chatroomService.getPrevious10Messages(moreMessagesRequest);
    }

    @MessageMapping(value = "/send")
    @SendTo(value = "/topic/public-room")
    public Message sendMessage(@RequestBody Message message, @Header("simpSessionId") String sessionId) {
        logger.log(Level.INFO, String.format("%s has just sent the message %s", message.getSender(), message.getContent()));
        return chatroomService.processMessage(message);
    }

    @MessageMapping(value = "/previous-messages")
    public void getPreviousMessages(@RequestBody MoreMessagesRequest moreMessagesRequest, @Header("simpSessionId") String sessionId) {
        logger.log(Level.INFO, String.format("User with session id:%s made a request for more previous messages", sessionId));
        simpMessagingTemplate.convertAndSend("/queue/" + sessionId, chatroomService.getPrevious10Messages(moreMessagesRequest));
    }
}
