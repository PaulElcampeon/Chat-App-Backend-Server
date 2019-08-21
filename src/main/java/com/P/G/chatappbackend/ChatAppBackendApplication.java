package com.P.G.chatappbackend;

//import com.P.G.chatappbackend.services.PrivateChatRoomService;

import com.P.G.chatappbackend.services.PublicChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class ChatAppBackendApplication {

    @Autowired
    private PublicChatRoomService publicChatRoomService;

//	@Autowired
//	private PrivateChatRoomService privateChatRoomService;

    public static void main(String[] args) {
        SpringApplication.run(ChatAppBackendApplication.class, args);
    }

    @PostConstruct
    public void init() {
        publicChatRoomService.initializeNameCache();
//		privateChatRoomService.initializeNames();
    }
}
