package com.P.G.chatappbackend;

import com.P.G.chatappbackend.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class ChatAppBackendApplication {

    @Autowired
    private RoomService roomService;

    public static void main(String[] args) {
        SpringApplication.run(ChatAppBackendApplication.class, args);
    }


    @PostConstruct
    public void init() {
        roomService.initializeNameCache();
    }

}
